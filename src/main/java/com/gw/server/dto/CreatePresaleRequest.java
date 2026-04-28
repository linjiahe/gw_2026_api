package com.gw.server.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("创建预售记录请求")
public class CreatePresaleRequest {

    @ApiModelProperty(value = "NFT等级ID", example = "1", required = true)
    @NotNull(message = "NFT等级ID不能为空")
    private Integer nftLevelId;

    @ApiModelProperty(value = "购买数量", example = "2", required = true)
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

    @ApiModelProperty(value = "转账哈希", example = "3", required = true)
    @NotNull(message = "转账哈希不能为空")
    private Integer haxi;
}
