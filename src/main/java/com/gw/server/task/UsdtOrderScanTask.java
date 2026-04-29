//package com.gw.server.task;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Slf4j
//@Component
//public class UsdtOrderScanTask {
//
//    @Autowired
//    private BscUsdtVerifierService bscUsdtVerifierService; // 上一次我们写的验证逻辑类
//
//    @Autowired
//    private OrderMapper orderMapper; // 你的数据库操作 Mapper
//
//    // 最大重试次数（如果 5 秒查一次，30 次就是 2.5 分钟。超过这个时间查不到直接判死刑）
//    private static final int MAX_RETRY_COUNT = 30;
//
//    /**
//     * 每 5 秒执行一次
//     * 注意：生产环境如果部署了多台服务器，这里必须加分布式锁（如 Redis 分布式锁/Redisson），
//     * 否则多台机器会同时查同一笔订单！
//     */
//    @Scheduled(fixedDelay = 5000)
//    public void scanPendingOrders() {
//        // 1. 每次只捞取前 50 条 PENDING 状态的订单，防止一次性把内存撑爆或把 RPC 节点 QPS 打满
//        List<OrderEntity> pendingOrders = orderMapper.selectPendingOrders(50);
//
//        if (pendingOrders == null || pendingOrders.isEmpty()) {
//            return; // 没有需要处理的订单，直接返回
//        }
//
//        log.info("开始扫描等待链上确认的 USDT 订单，本次扫到 {} 条", pendingOrders.size());
//
//        for (OrderEntity order : pendingOrders) {
//            try {
//                // 单个订单的处理逻辑抽离，方便加事务
//                processSingleOrder(order);
//            } catch (Exception e) {
//                log.error("处理订单异常，订单号: {}, 交易哈希: {}", order.getOrderNo(), order.getTxHash(), e);
//            }
//        }
//    }
//
//    /**
//     * 处理单笔订单的逻辑
//     * 注意：这里加 @Transactional 是为了保证修改订单状态和给用户加钱（上分）是原子操作
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void processSingleOrder(OrderEntity order) {
//        String txHash = order.getTxHash();
//
//        // 1. 防御性编程：再次确认订单确实是 PENDING 状态
//        if (!"PENDING".equals(order.getStatus())) {
//            return;
//        }
//
//        // 2. 检查重试次数是否已经超标
//        if (order.getRetryCount() >= MAX_RETRY_COUNT) {
//            log.warn("哈希 [{}] 重试已达上限 {} 次，判定为超时失败", txHash, MAX_RETRY_COUNT);
//            orderMapper.updateStatusAndRemark(order.getId(), "FAILED", "链上查询超时");
//            return;
//        }
//
//        // 3. 调用 Web3j 验证引擎（上个回答里的那个类）
//        VerifyStatus verifyStatus = bscUsdtVerifierService.verify(
//                txHash,
//                order.getExpectedAddress(),
//                order.getExpectedAmountWei()
//        );
//
//        // 4. 根据不同的返回状态处理业务逻辑
//        switch (verifyStatus) {
//            case SUCCESS:
//                log.info("哈希 [{}] 验证成功！准备发放资金...", txHash);
//                // a. 更新订单状态为成功
//                int updatedRows = orderMapper.updateStatusToSuccess(order.getId(), "PENDING");
//
//                // 【核心防线】：必须利用乐观锁或状态条件，确保只有 1 行被更新成功，防止并发重复上分
//                if (updatedRows == 1) {
//                    // b. 给用户账户增加余额 (调用你的业务逻辑)
//                    // userService.addBalance(order.getUserId(), order.getAmount());
//                    log.info("哈希 [{}] 充值上分完成！", txHash);
//                } else {
//                    log.error("哈希 [{}] 状态更新失败，可能是并发导致，拒绝重复发钱！", txHash);
//                }
//                break;
//
//            case FAILED:
//                log.error("哈希 [{}] 验证失败（假币/金额不符/交易失败），订单关闭。", txHash);
//                orderMapper.updateStatusAndRemark(order.getId(), "FAILED", "校验不通过，涉嫌假币或金额不足");
//                break;
//
//            case PENDING:
//                log.info("哈希 [{}] 暂未查到或网络波动，增加重试次数。", txHash);
//                orderMapper.incrementRetryCount(order.getId());
//                break;
//        }
//    }
//}