package com.gw.server.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CreatePresaleRequest {

    @NotNull(message = "NFT等级ID不能为空")
    private Integer nftLevelId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
}
