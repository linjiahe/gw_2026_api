package com.gw.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gw.server.entity.User;
import com.gw.server.exception.BusinessException;
import com.gw.server.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User getUserById(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    public User getUserByAddress(String address) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getWalletAddress, address.toLowerCase())
        );
    }
}
