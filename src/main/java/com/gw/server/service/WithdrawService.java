package com.gw.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gw.server.dto.ApplyWithdrawRequest;
import com.gw.server.dto.PageResult;
import com.gw.server.entity.UserAddress;
import com.gw.server.entity.Withdraw;
import com.gw.server.exception.BusinessException;
import com.gw.server.mapper.UserAddressMapper;
import com.gw.server.mapper.WithdrawMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WithdrawService {

    private final WithdrawMapper withdrawMapper;

    private final UserAddressMapper userAddressMapper; // 注入资产 Mapper

    public WithdrawService(WithdrawMapper withdrawMapper, UserAddressMapper userAddressMapper) {
        this.withdrawMapper = withdrawMapper;
        this.userAddressMapper = userAddressMapper;
    }

    /**
     * 获取我的提现记录（分页）
     */
    public PageResult<Withdraw> getMyWithdraws(String walletAddress, long pageNum, long pageSize) {
        // withdraw 表里明确存的是 wallet_address[cite: 6]
        Page<Withdraw> page = withdrawMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Withdraw>()
                        .eq(Withdraw::getWalletAddress, walletAddress.toLowerCase())
                        .orderByDesc(Withdraw::getCreateTime)
        );
        return PageResult.of(page);
    }

    // ========== 2. 申请提现核心逻辑 (内扣模式) ==========
    @Transactional(rollbackFor = Exception.class)
    public void applyWithdraw(String walletAddress, ApplyWithdrawRequest request) {
        String coin = request.getCoin().toUpperCase();

        // 1. 用户申请提现的总额 (例如 100 USDT)
        BigDecimal applyTotalAmount = request.getBalance();

        // 2. 查询用户该币种的资产
        UserAddress userAsset = userAddressMapper.selectOne(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getAddress, walletAddress.toLowerCase())
                        .eq(UserAddress::getCoin, coin)
        );

        // 3. 校验余额是否充足 (申请 100，余额必须 >= 100)
        if (userAsset == null || userAsset.getBalance().compareTo(applyTotalAmount) < 0) {
            throw new BusinessException("您的 " + coin + " 余额不足，当前可用余额: " + (userAsset != null ? userAsset.getBalance().stripTrailingZeros().toPlainString() : "0"));
        }

        // 4. 计算手续费 (假设 5%)
        BigDecimal sxf = applyTotalAmount.multiply(new BigDecimal("0.00"));

        // 5. 计算实际应到账金额 (例如 100 - 5 = 95 USDT)
        // 提示：这个变量（actualArrivalAmount）在目前你的 withdraw 表结构中没有单独的字段存，
        // 但我们在后台审核时可以通过 withdraw.balance - withdraw.sxf_balance 算出来。
        // BigDecimal actualArrivalAmount = applyTotalAmount.subtract(sxf);

        // 6. 安全扣减余额 (从账户里实打实扣掉 100)
        int updated = userAddressMapper.deductBalance(userAsset.getId(), applyTotalAmount);
        if (updated == 0) {
            throw new BusinessException("扣减余额失败，请稍后重试 (可能是并发导致)");
        }

        // 7. 生成提现记录 (状态 0-未审核)
        Withdraw withdraw = new Withdraw();
        withdraw.setCoin(coin);
        withdraw.setWalletAddress(walletAddress.toLowerCase());
        withdraw.setStatus(0);

        // 【关键】：这里存的是用户申请扣除的总额 (100)
        // 后台审核人员看到这条记录时，知道用户申请扣了 100，其中手续费是 5。
        withdraw.setBalance(applyTotalAmount);
        withdraw.setSxfBalance(sxf);
        withdraw.setAddress(request.getAddress());
        withdraw.setCreateTime(LocalDateTime.now());
        withdraw.setUpdateTime(LocalDateTime.now());

        withdrawMapper.insert(withdraw);
    }
}