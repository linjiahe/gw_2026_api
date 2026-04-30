package com.gw.server.controller;

import com.gw.server.dto.PageResult;
import com.gw.server.dto.Result;
import com.gw.server.entity.JlRecord;
import com.gw.server.service.JlRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "奖励记录接口")
@RestController
@RequestMapping("/api/reward")
public class JlRecordController {

    private final JlRecordService jlRecordService;

    public JlRecordController(JlRecordService jlRecordService) {
        this.jlRecordService = jlRecordService;
    }

    @ApiOperation(value = "我的奖励记录（分页）", notes = "需在Header中携带Bearer Token。分页查询当前用户的奖励发放记录。")
    @GetMapping("/my")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", defaultValue = "1", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页条数", defaultValue = "10", dataType = "long", paramType = "query")
    })
    public Result<PageResult<JlRecord>> getMyRewards(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            HttpServletRequest httpRequest) {

        String address = (String) httpRequest.getAttribute("walletAddress");
        return Result.ok(jlRecordService.getMyRewards(address, page, size));
    }
}
