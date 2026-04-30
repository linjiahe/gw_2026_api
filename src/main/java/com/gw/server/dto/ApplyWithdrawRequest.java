package com.gw.server.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("申请提现请求参数")
public class ApplyWithdrawRequest {

    @NotBlank(message = "提现币种不能为空")
    @ApiModelProperty(value = "提现币种，目前前端固定USDT", example = "USDT", required = true)
    private String coin;

    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "1.00", message = "提现金额不能小于 1")
    @ApiModelProperty(value = "申请提现扣除总额 (实际到账会扣除手续费)", example = "100.00", required = true)
    private BigDecimal balance;

    @NotBlank(message = "到账地址不能为空")
    @ApiModelProperty(value = "提现到账地址", example = "0x89d2...", required = true)
    private String address;
}