package com.gw.server.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("团队信息响应")
public class TeamResponse {

    @ApiModelProperty(value = "直推用户钱包地址列表")
    private List<String> addresses;

    @ApiModelProperty(value = "团队总人数（含多级下级）", example = "15")
    private Integer teamCount;

    @ApiModelProperty(value = "直推人数（仅一级）", example = "3")
    private Integer directCount;

    @ApiModelProperty(value = "团队NFT总数量（含预售）", example = "42")
    private Integer teamNftCount;

    @ApiModelProperty(value = "直推NFT总数量（含预售）", example = "10")
    private Integer directNftCount;
}
