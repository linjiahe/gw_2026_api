package com.gw.server.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("获取nonce请求")
public class NonceRequest {

    @ApiModelProperty(value = "以太坊钱包地址", example = "0x71C7656EC7ab88b098defB751B7401B5f6d8976F", required = true)
    @NotBlank(message = "钱包地址不能为空")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "钱包地址格式不正确")
    private String address;
}
