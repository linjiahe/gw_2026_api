package com.gw.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("nft_address")
public class NftAddress {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String address;
}
