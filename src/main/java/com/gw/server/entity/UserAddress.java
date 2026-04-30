package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_address")
@ApiModel("用户资产表")
public class UserAddress {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户ID", example = "1001")
    private Integer userId;

    @ApiModelProperty(value = "钱包地址", example = "0x71c7...")
    private String address;

    @ApiModelProperty(value = "币种", example = "USDT")
    private String coin;

    @ApiModelProperty(value = "余额", example = "500.00")
    private BigDecimal balance;

    @ApiModelProperty(value = "创建时间", example = "2026-04-30T12:00:00")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间", example = "2026-04-30T12:00:00")
    private LocalDateTime updatedAt;
}