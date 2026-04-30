package com.gw.server.controller;

import com.gw.server.dto.ApplyWithdrawRequest;
import com.gw.server.dto.PageResult;
import com.gw.server.dto.Result;
import com.gw.server.entity.Withdraw;
import com.gw.server.service.WithdrawService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "提现记录接口")
@RestController
@RequestMapping("/api/withdraw")
public class WithdrawController {

    private final WithdrawService withdrawService;

    public WithdrawController(WithdrawService withdrawService) {
        this.withdrawService = withdrawService;
    }

    @ApiOperation(value = "我的提现记录（分页）", notes = "需在Header中携带Bearer Token。分页查询当前用户的提现历史及状态。")
    @GetMapping("/my")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", defaultValue = "1", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页条数", defaultValue = "10", dataType = "long", paramType = "query")
    })
    public Result<PageResult<Withdraw>> getMyWithdraws(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            HttpServletRequest httpRequest) {

        String address = (String) httpRequest.getAttribute("walletAddress");
        return Result.ok(withdrawService.getMyWithdraws(address, page, size));
    }

    @ApiOperation(value = "申请提现", notes = "需在Header中携带Bearer Token。发起一笔提现申请，系统会预扣除可用余额。")
    @PostMapping("/apply")
    public Result<Void> applyWithdraw(@Valid @RequestBody ApplyWithdrawRequest request,
                                      HttpServletRequest httpRequest) {

        String address = (String) httpRequest.getAttribute("walletAddress");
        withdrawService.applyWithdraw(address, request);
        return Result.ok();
    }
}