package com.gw.server.web3;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gw.server.entity.NftAddress;
import com.gw.server.entity.NftRecord;
import com.gw.server.mapper.NftAddressMapper;
import com.gw.server.mapper.NftRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NftRecordScanTask {

    private final NftRecordMapper nftRecordMapper;
    private final NftAddressMapper nftAddressMapper; // 用于获取官方收款地址池
    private final BscUsdtVerifierService verifierService; // 我们的链上核心验钞机

    // 最大重试次数 (假设 10 秒查一次，30次就是 5 分钟。5分钟还没出块基本可以宣告失败了)
    private static final int MAX_RETRY_COUNT = 30;

    public NftRecordScanTask(NftRecordMapper nftRecordMapper,
                             NftAddressMapper nftAddressMapper,
                             BscUsdtVerifierService verifierService) {
        this.nftRecordMapper = nftRecordMapper;
        this.nftAddressMapper = nftAddressMapper;
        this.verifierService = verifierService;
    }

    /**
     * 定时任务：每 10 秒扫描一次待确认的 NFT 订单
     */
    @Scheduled(fixedDelay = 10000)
    public void scanPendingNftRecords() {
        // 1. 查询待确认 (status = 0) 的订单，每次捞取 50 条防止把内存撑爆
        LambdaQueryWrapper<NftRecord> query = new LambdaQueryWrapper<>();
        query.eq(NftRecord::getStatus, 0)
                .orderByAsc(NftRecord::getCreatedAt)
                .last("LIMIT 50");

        List<NftRecord> pendingRecords = nftRecordMapper.selectList(query);

        if (pendingRecords == null || pendingRecords.isEmpty()) {
            return; // 闲时无订单，直接返回
        }

        // 2. 获取全量合法的“官方收款地址池”
        List<String> validAddresses = nftAddressMapper.selectList(null)
                .stream()
                .map(NftAddress::getAddress)
                .collect(Collectors.toList());

        // 【安全拦截】：如果地址表被误删空了，必须立刻停止验证，防止错判！
        if (validAddresses.isEmpty()) {
            log.error("【严重告警】NftAddress 表中未配置任何官方收款地址，扫描任务中止！");
            return;
        }

        log.info("开始扫描，获取到 {} 个官方收款地址，本次处理 {} 条待确认订单", validAddresses.size(), pendingRecords.size());

        // 3. 遍历处理每一个订单
        for (NftRecord record : pendingRecords) {
            try {
                // 单条记录独立处理，方便内部加事务和捕获异常，互不影响
                processSingleRecord(record, validAddresses);
            } catch (Exception e) {
                log.error("处理 NFT 订单异常，ID: {}, 哈希: {}", record.getId(), record.getHaxi(), e);
            }
        }
    }

    /**
     * 处理单条订单的逻辑
     * @Transactional 保证状态更新和发卡逻辑是原子的
     */
    @Transactional(rollbackFor = Exception.class)
    public void processSingleRecord(NftRecord record, List<String> validAddresses) {
        String haxi = record.getHaxi();

        // 1. 检查重试次数，防止因网络问题造成的死循环查询
        if (record.getRetryCount() != null && record.getRetryCount() >= MAX_RETRY_COUNT) {
            log.warn("NFT订单 [{}] 哈希 [{}] 链上查询重试达上限 {} 次，标记为校验失败", record.getId(), haxi, MAX_RETRY_COUNT);
            updateRecordStatus(record.getId(), 0, -1); // -1 代表校验失败/已关闭
            return;
        }

        // 2. 将订单记录中的 USDT 价格 (18位精度) 转换为链上实际比对需要的 BigInteger 格式
        BigInteger expectedAmountWei = convertUsdtToWei(record.getAmount());

        // 3. 呼叫验证引擎（传入：哈希，官方地址池，预期金额）
        VerifyStatus status = verifierService.verify(haxi, validAddresses, expectedAmountWei);

        // 4. 根据链上真实情况，流转订单状态
        switch (status) {
            case SUCCESS:
                // 【核心防线】：必须判断 updateRecordStatus 返回 true，确保旧状态确实是 0 才发货！
                if (updateRecordStatus(record.getId(), 0, 1)) {
                    log.info("【收款成功】哈希 [{}] 完美匹配！准备为用户 [{}] 发放 NFT...", haxi, record.getWalletAddress());

                    // =========================================================
                    // TODO: 在这里编写你给用户真正上分、铸造/转移 NFT 的业务逻辑
                    // nftService.mintOrTransferNft(record.getWalletAddress(), record.getNftLevelId());
                    // =========================================================
                } else {
                    log.warn("哈希 [{}] 验证成功，但订单状态更新失败（可能被并发处理了）", haxi);
                }
                break;

            case FAILED:
                log.error("【收款失败】哈希 [{}] 校验未通过（涉嫌假币/打给私人/金额不足），订单废弃", haxi);
                updateRecordStatus(record.getId(), 0, -1);
                break;

            case PENDING:
                log.info("【链上确认中】哈希 [{}] 暂未打包或 RPC 节点延迟，增加重试次数", haxi);
                // 调用我们之前在 Mapper 里写好的自增方法
                nftRecordMapper.incrementRetry(record.getId());
                break;
        }
    }

    // ==================== 内部辅助工具方法 ====================

    /**
     * 乐观锁更新订单状态
     * 只有当数据库里这条记录的 status 依然是 oldStatus 时，才允许更新，防并发神器
     */
    private boolean updateRecordStatus(Long id, Integer oldStatus, Integer newStatus) {
        LambdaUpdateWrapper<NftRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NftRecord::getId, id)
                .eq(NftRecord::getStatus, oldStatus)
                .set(NftRecord::getStatus, newStatus)
                .set(NftRecord::getUpdatedAt, LocalDateTime.now());

        // 返回是否至少有一行数据被成功更新
        return nftRecordMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 将业务金额 (如 100 USDT) 乘以 10 的 18 次方，转为链上 Wei 的格式
     */
    private BigInteger convertUsdtToWei(BigDecimal amount) {
        return amount.multiply(BigDecimal.TEN.pow(18)).toBigInteger();
    }
}