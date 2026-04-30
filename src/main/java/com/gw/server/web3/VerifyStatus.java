package com.gw.server.web3;

/**
 * 链上交易验证状态枚举
 */
public enum VerifyStatus {
    SUCCESS, // 验证成功：钱是真的，且到了我们的官方池子，金额足够
    PENDING, // 验证中：还在链上排队打包，或者RPC节点网络波动，需要等会儿再查
    FAILED   // 验证失败：假币、交易失败、金额不够、或者打给了别人（直接废弃订单）
}