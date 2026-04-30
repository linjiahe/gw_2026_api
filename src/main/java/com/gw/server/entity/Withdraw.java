package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("withdraw")
@ApiModel("提现表")
public class Withdraw {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id", example = "1234567890")
    private String id;

    @ApiModelProperty(value = "币种", example = "USDT")
    private String coin;

    @ApiModelProperty(value = "用户地址", example = "0x71c7...")
    private String walletAddress;

    @ApiModelProperty(value = "状态（0:未审核 1:审核通过 2:审核不通过）", example = "0")
    private Integer status;

    @ApiModelProperty(value = "余额", example = "100.00")
    private BigDecimal balance;

    @ApiModelProperty(value = "手续费", example = "2.00")
    private BigDecimal sxfBalance;

    @ApiModelProperty(value = "到账地址", example = "0x89d2...")
    private String address;

    @ApiModelProperty(value = "创建时间", example = "2026-04-30T12:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间", example = "2026-04-30T12:00:00")
    private LocalDateTime updateTime;
}