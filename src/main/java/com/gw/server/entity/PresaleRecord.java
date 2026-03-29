package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("presale_record")
public class PresaleRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer nftLevelId;

    private String walletAddress;

    private BigDecimal amount;

    private Integer quantity;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
