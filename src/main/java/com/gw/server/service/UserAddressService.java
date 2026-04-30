package com.gw.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gw.server.entity.UserAddress;
import com.gw.server.mapper.UserAddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAddressService {

    private final UserAddressMapper userAddressMapper;

    public UserAddressService(UserAddressMapper userAddressMapper) {
        this.userAddressMapper = userAddressMapper;
    }

    /**
     * 获取我的资产列表（返回所有币种的余额）
     */
    public List<UserAddress> getMyAssets(String walletAddress) {
        // user_address 表里存的是 address[cite: 6]
        return userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getAddress, walletAddress.toLowerCase())
        );
    }
}
