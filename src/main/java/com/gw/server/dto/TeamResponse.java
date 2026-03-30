package com.gw.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {

    private List<String> addresses;

    private Integer teamCount;

    private Integer directCount;

    private Integer teamNftCount;

    private Integer directNftCount;
}
