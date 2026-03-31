package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("wallet_address")
    private String walletAddress;

    private String nonce;

    private Integer status;

    @TableField("invite_code")
    private String inviteCode;

    @TableField("invited_by")
    private Integer invitedBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;
}
