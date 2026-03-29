package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("nft_level")
public class NftLevel {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private BigDecimal price;

    private Integer totalQuantity;

    private Integer remainingQuantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
