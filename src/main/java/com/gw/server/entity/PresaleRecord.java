package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("presale_record")
@ApiModel("预售记录")
public class PresaleRecord {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "记录ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "NFT等级ID", example = "1")
    private Integer nftLevelId;

    @ApiModelProperty(value = "买家钱包地址", example = "0x71c7...")
    private String walletAddress;

    @ApiModelProperty(value = "支付金额", example = "50.00")
    private BigDecimal amount;

    @ApiModelProperty(value = "购买数量", example = "1")
    private Integer quantity;

    @ApiModelProperty(value = "交易哈希", example = "0x71c7...")
    private String haxi;

    @ApiModelProperty(value = "0-待确认，1-待铸造 2-已铸造 ", example = "0")
    private Integer status;

    @ApiModelProperty(value = "创建时间", example = "2026-03-29T10:00:00")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间", example = "2026-03-29T10:00:00")
    private LocalDateTime updatedAt;
}
