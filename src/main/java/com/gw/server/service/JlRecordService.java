package com.gw.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gw.server.dto.PageResult;
import com.gw.server.entity.JlRecord;
import com.gw.server.mapper.JlRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class JlRecordService {

    private final JlRecordMapper jlRecordMapper;

    public JlRecordService(JlRecordMapper jlRecordMapper) {
        this.jlRecordMapper = jlRecordMapper;
    }

    /**
     * 获取我的奖励记录（分页）
     */
    public PageResult<JlRecord> getMyRewards(String walletAddress, long pageNum, long pageSize) {
        // ⚠️注意：jl_record 表结构里叫做 user_id[cite: 5]。
        // 这里假设你的业务逻辑里 user_id 存的就是钱包地址（转小写比对）。
        // 如果存的是真实的数字 ID，请先根据钱包地址去查出数字 ID，再传给下面：
        Page<JlRecord> page = jlRecordMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<JlRecord>()
                        .eq(JlRecord::getUserId, walletAddress.toLowerCase())
                        .orderByDesc(JlRecord::getCreateTime)
        );
        return PageResult.of(page);
    }
}