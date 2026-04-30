package com.gw.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gw.server.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {

    /**
     * 安全扣减余额（利用数据库底层原子性，且要求余额必须大于等于扣款金额）
     */
    @Update("UPDATE user_address SET balance = balance - #{amount}, updated_at = NOW() WHERE id = #{id} AND balance >= #{amount}")
    int deductBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);
}