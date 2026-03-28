package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("nonce_record")
public class NonceRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String walletAddress;

    private String nonce;

    private Integer used;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
