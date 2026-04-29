package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("nft_record")
@ApiModel("NFT购买记录")
public class NftRecord {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "记录ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "NFT等级ID", example = "1")
    private Integer nftLevelId;

    @ApiModelProperty(value = "买家钱包地址", example = "0x71c7...")
    private String walletAddress;

    @ApiModelProperty(value = "支付金额", example = "100.00")
    private BigDecimal amount;

    @ApiModelProperty(value = "购买数量", example = "2")
        private Integer quantity;

    @ApiModelProperty(value = "0-待确认，1-已完成 2-已链上铸造", example = "0")
    private Integer status;

    @ApiModelProperty(value = "创建时间", example = "2026-03-30T12:00:00")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间", example = "2026-03-30T12:00:00")
    private LocalDateTime updatedAt;
}
