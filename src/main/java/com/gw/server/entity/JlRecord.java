package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jl_record")
@ApiModel("奖励记录表")
public class JlRecord {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id", example = "1234567890")
    private String id;

    @ApiModelProperty(value = "币种", example = "USDT")
    private String coin;

    @ApiModelProperty(value = "用户ID", example = "1001")
    private String userId;

    @ApiModelProperty(value = "余额", example = "100.00")
    private BigDecimal balance;

    @ApiModelProperty(value = "状态(0：未发放，1:已发放)", example = "0")
    private Integer status;

    @ApiModelProperty(value = "来源（1: 直推奖励）", example = "1")
    private Integer sourceType;

    @ApiModelProperty(value = "创建时间", example = "2026-04-30T12:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间", example = "2026-04-30T12:00:00")
    private LocalDateTime updateTime;

    @Version
    @ApiModelProperty(value = "版本号（乐观锁）", example = "0")
    private Integer version;
}