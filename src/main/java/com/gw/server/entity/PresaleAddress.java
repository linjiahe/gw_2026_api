package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("presale_address")
public class PresaleAddress {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String address;
}
