package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("nft_level")
@ApiModel(description = "NFT等级信息")
public class NftLevel {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "等级ID", example = "1")
    private Integer id;

    @ApiModelProperty(value = "价格（USDT）", example = "99.00")
    private BigDecimal price;

    @ApiModelProperty(value = "总量", example = "10000")
    private Integer totalQuantity;

    @ApiModelProperty(value = "剩余数量", example = "8560")
    private Integer remainingQuantity;

    @ApiModelProperty(value = "创建时间", example = "2026-01-01T00:00:00")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间", example = "2026-04-27T10:00:00")
    private LocalDateTime updatedAt;
}
