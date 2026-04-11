package com.gw.server.dto;

import com.gw.server.entity.NftRecord;
import com.gw.server.entity.PresaleRecord;
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
@ApiModel("地址NFT详情响应")
public class AddressNftDetailResponse {

    @ApiModelProperty(value = "NFT购买记录列表")
    private List<NftRecord> nftRecords;

    @ApiModelProperty(value = "预售记录列表")
    private List<PresaleRecord> presaleRecords;
}
