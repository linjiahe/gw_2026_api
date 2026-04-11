package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
@ApiModel("用户")
public class User {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "用户ID", example = "1")
    private Integer id;

    @TableField("wallet_address")
    @ApiModelProperty(value = "钱包地址", example = "0x71c7656ec7ab88b098defb751b7401b5f6d8976f")
    private String walletAddress;

    @ApiModelProperty(value = "签名随机数（内部使用）", hidden = true)
    private String nonce;

    @ApiModelProperty(value = "账号状态：1-正常，0-禁用", example = "1")
    private Integer status;

    @TableField("invite_code")
    @ApiModelProperty(value = "用户邀请码", example = "K3MNPQR7")
    private String inviteCode;

    @TableField("invited_by")
    @ApiModelProperty(value = "邀请人用户ID", example = "1")
    private Integer invitedBy;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间", example = "2026-03-30T10:00:00")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间", example = "2026-03-30T12:00:00")
    private LocalDateTime updatedAt;

    @ApiModelProperty(value = "最后登录时间")
    private LocalDateTime lastLoginAt;
}
