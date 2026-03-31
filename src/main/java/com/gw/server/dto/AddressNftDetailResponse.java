package com.gw.server.dto;

import com.gw.server.entity.NftRecord;
import com.gw.server.entity.PresaleRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressNftDetailResponse {

    private List<NftRecord> nftRecords;

    private List<PresaleRecord> presaleRecords;
}
