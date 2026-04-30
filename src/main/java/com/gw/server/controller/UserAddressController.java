package com.gw.server.controller;

import com.gw.server.dto.Result;
import com.gw.server.entity.UserAddress;
import com.gw.server.service.UserAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "用户资产接口")
@RestController
@RequestMapping("/api/asset")
public class UserAddressController {

    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @ApiOperation(value = "我的资产列表", notes = "需在Header中携带Bearer Token。查询当前登录用户的所有币种资产及余额。")
    @GetMapping("/my")
    public Result<List<UserAddress>> getMyAssets(HttpServletRequest httpRequest) {

        String address = (String) httpRequest.getAttribute("walletAddress");
        return Result.ok(userAddressService.getMyAssets(address));
    }
}