package com.gw.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gw.server.dto.PageResult;
import com.gw.server.entity.*;
import com.gw.server.exception.BusinessException;
import com.gw.server.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class NftService {

    private final NftLevelMapper nftLevelMapper;
    private final PresaleRecordMapper presaleRecordMapper;
    private final NftRecordMapper nftRecordMapper;
    private final PresaleAddressMapper presaleAddressMapper;
    private final NftAddressMapper nftAddressMapper;

    public NftService(NftLevelMapper nftLevelMapper,
                      PresaleRecordMapper presaleRecordMapper,
                      NftRecordMapper nftRecordMapper,
                      PresaleAddressMapper presaleAddressMapper,
                      NftAddressMapper nftAddressMapper) {
        this.nftLevelMapper = nftLevelMapper;
        this.presaleRecordMapper = presaleRecordMapper;
        this.nftRecordMapper = nftRecordMapper;
        this.presaleAddressMapper = presaleAddressMapper;
        this.nftAddressMapper = nftAddressMapper;
    }

    // ========== 1. 创建预售记录 ==========
    @Transactional
    public void createPresaleRecord(String walletAddress, Integer nftLevelId, Integer quantity) {
        NftLevel level = getLevelOrThrow(nftLevelId);
        BigDecimal amount = level.getPrice().multiply(BigDecimal.valueOf(quantity));

        PresaleRecord record = new PresaleRecord();
        record.setNftLevelId(nftLevelId);
        record.setWalletAddress(walletAddress.toLowerCase());
        record.setAmount(amount);
        record.setQuantity(quantity);
        record.setStatus(0);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        presaleRecordMapper.insert(record);
    }

    // ========== 2. 创建NFT记录 ==========
    @Transactional
    public void createNftRecord(String walletAddress, Integer nftLevelId, Integer quantity) {
        NftLevel level = getLevelOrThrow(nftLevelId);
        BigDecimal amount = level.getPrice().multiply(BigDecimal.valueOf(quantity));

        NftRecord record = new NftRecord();
        record.setNftLevelId(nftLevelId);
        record.setWalletAddress(walletAddress.toLowerCase());
        record.setAmount(amount);
        record.setQuantity(quantity);
        record.setStatus(0);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        nftRecordMapper.insert(record);
    }

    // ========== 3. 随机预售地址 ==========
    public String getRandomPresaleAddress() {
        PresaleAddress addr = presaleAddressMapper.selectOne(
                new LambdaQueryWrapper<PresaleAddress>().last("ORDER BY RAND() LIMIT 1")
        );
        return addr != null ? addr.getAddress() : null;
    }

    // ========== 4. 随机NFT地址 ==========
    public String getRandomNftAddress() {
        NftAddress addr = nftAddressMapper.selectOne(
                new LambdaQueryWrapper<NftAddress>().last("ORDER BY RAND() LIMIT 1")
        );
        return addr != null ? addr.getAddress() : null;
    }

    // ========== 5. 预售记录全网前20 ==========
    public List<PresaleRecord> getPresaleRecords() {
        Page<PresaleRecord> page = presaleRecordMapper.selectPage(
                new Page<>(1, 20),
                new LambdaQueryWrapper<PresaleRecord>().orderByDesc(PresaleRecord::getCreatedAt)
        );
        return page.getRecords();
    }

    // ========== 6. NFT记录全网前20 ==========
    public List<NftRecord> getNftRecords() {
        Page<NftRecord> page = nftRecordMapper.selectPage(
                new Page<>(1, 20),
                new LambdaQueryWrapper<NftRecord>().orderByDesc(NftRecord::getCreatedAt)
        );
        return page.getRecords();
    }

    // ========== 7. 我的预售记录（分页） ==========
    public PageResult<PresaleRecord> getMyPresaleRecords(String walletAddress, long pageNum, long pageSize) {
        Page<PresaleRecord> page = presaleRecordMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<PresaleRecord>()
                        .eq(PresaleRecord::getWalletAddress, walletAddress.toLowerCase())
                        .orderByDesc(PresaleRecord::getCreatedAt)
        );
        return PageResult.of(page);
    }

    // ========== 8. 我的NFT记录（分页） ==========
    public PageResult<NftRecord> getMyNftRecords(String walletAddress, long pageNum, long pageSize) {
        Page<NftRecord> page = nftRecordMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<NftRecord>()
                        .eq(NftRecord::getWalletAddress, walletAddress.toLowerCase())
                        .orderByDesc(NftRecord::getCreatedAt)
        );
        return PageResult.of(page);
    }

    // ========== 9. NFT等级列表 ==========
    public List<NftLevel> getNftLevels() {
        return nftLevelMapper.selectList(
                new LambdaQueryWrapper<NftLevel>().orderByAsc(NftLevel::getId)
        );
    }

    private NftLevel getLevelOrThrow(Integer nftLevelId) {
        NftLevel level = nftLevelMapper.selectById(nftLevelId);
        if (level == null) {
            throw new BusinessException("NFT等级不存在");
        }
        return level;
    }
}
