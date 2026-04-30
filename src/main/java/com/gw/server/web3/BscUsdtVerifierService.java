package com.gw.server.web3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BscUsdtVerifierService {

    private final Web3j web3j;

    // BSC链上官方正版 USDT 的合约地址 (校验它防止别人用假币发空投)
    private static final String USDT_CONTRACT = "0x55d398326f99059fF775485246999027B3197955";

    // ERC-20 代币 Transfer(address,address,uint256) 事件的 Keccak-256 特征码
    private static final String TRANSFER_EVENT_TOPIC = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

    // 通过构造器注入 Web3j
    public BscUsdtVerifierService(Web3j web3j) {
        this.web3j = web3j;
    }

    /**
     * 核心校验逻辑
     *
     * @param txHash            前端传来的交易哈希
     * @param validReceivers    我们的官方收款地址池 (只要打进这里面任何一个都算成功)
     * @param expectedAmountWei 预期收到的金额 (注意：已经是乘过 10^18 的 wei 单位)
     * @return VerifyStatus 验证结果
     */
    public VerifyStatus verify(String txHash, List<String> validReceivers, BigInteger expectedAmountWei) {
        try {
            // 1. 去节点拉取交易回执 (Receipt)
            Optional<TransactionReceipt> receiptOpt = web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt();

            // 如果为空，说明这笔交易还在内存池里飘着，矿工还没打包
            if (!receiptOpt.isPresent()) {
                log.info("哈希 [{}] 暂未被打包入块，返回 PENDING", txHash);
                return VerifyStatus.PENDING;
            }

            TransactionReceipt receipt = receiptOpt.get();

            // 2. 检查交易本身是否成功 (失败的交易也会产生哈希并扣除gas，必须拦截)
            if (!"0x1".equals(receipt.getStatus())) {
                log.error("哈希 [{}] 在链上执行失败 (Status为0x0)", txHash);
                return VerifyStatus.FAILED;
            }

            // 3. 检查这笔交易调用的合约是不是官方的 USDT 合约 (防空气假币攻击)
            if (!USDT_CONTRACT.equalsIgnoreCase(receipt.getTo())) {
                log.error("哈希 [{}] 涉嫌假币攻击！目标合约 {} 不是官方 USDT", txHash, receipt.getTo());
                return VerifyStatus.FAILED;
            }

            // 4. 解析底层事件日志，寻找真正的资金流向
            for (Log eventLog : receipt.getLogs()) {

                // 双重保险：确保这条日志确实是由 USDT 合约发出的
                if (!USDT_CONTRACT.equalsIgnoreCase(eventLog.getAddress())) {
                    continue;
                }

                // 检查是不是 Transfer 事件
                List<String> topics = eventLog.getTopics();
                if (topics == null || topics.isEmpty() || !TRANSFER_EVENT_TOPIC.equals(topics.get(0))) {
                    continue;
                }

                // ERC20 Transfer 日志的标准格式：
                // topics[0] = 事件签名
                // topics[1] = 发送方 (from)
                // topics[2] = 接收方 (to)
                // data = 转账金额
                if (topics.size() >= 3) {

                    // 解析出真实的收款人地址。链上地址带有补零，总长66位，需要截取最后40位并加上 0x
                    String actualReceiver = "0x" + topics.get(2).substring(26);

                    // 解析实际到账的金额
                    BigInteger actualAmountWei = Numeric.decodeQuantity(eventLog.getData());

                    // 5. 终极比对：金额够不够？是不是打给我们的？
                    boolean isReceiverValid = validReceivers.stream()
                            .anyMatch(addr -> addr.equalsIgnoreCase(actualReceiver));

                    if (isReceiverValid && actualAmountWei.compareTo(expectedAmountWei) >= 0) {
                        return VerifyStatus.SUCCESS; // 完美！无懈可击！
                    } else if (!isReceiverValid) {
                        log.error("哈希 [{}] 验证失败：款项被转到了私人地址 {}", txHash, actualReceiver);
                        // 不直接返回，因为一笔交易里可能会转多笔帐（比如同时触发了手续费分发等），继续遍历下一个 Log
                    } else {
                        log.error("哈希 [{}] 验证失败：金额不足！预期: {}, 实际: {}", txHash, expectedAmountWei, actualAmountWei);
                        // 金额不够，继续遍历（同上理由）
                    }
                }
            }

            // 遍历完所有日志都没找到符合我们条件的转账
            log.error("哈希 [{}] 未找到匹配的资金流入记录", txHash);
            return VerifyStatus.FAILED;

        } catch (Exception e) {
            // 不要直接返回 FAILED。有可能是你的网络卡了，或者免费 RPC 节点抽风了限流了。
            // 返回 PENDING 让外层的定时任务稍后重试。
            log.error("请求 BSC 节点验证异常，哈希 [{}], 原因: {}", txHash, e.getMessage());
            return VerifyStatus.PENDING;
        }
    }
}