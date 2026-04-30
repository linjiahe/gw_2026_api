package com.gw.server.web3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {

    // 这里使用的是币安官方的公共免费节点。
    // 生产环境强烈建议换成付费的商业节点 (如 QuickNode, Alchemy)，否则请求过快会被封 IP
    private static final String BSC_RPC_URL = "https://bsc-dataseed.binance.org/";

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(BSC_RPC_URL));
    }
}
