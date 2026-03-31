package com.gw.server.controller;

import com.gw.server.dto.AddressNftDetailResponse;
import com.gw.server.dto.Result;
import com.gw.server.dto.TeamResponse;
import com.gw.server.entity.User;
import com.gw.server.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "用户接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userService.getUserById(userId);

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("address", user.getWalletAddress());
        profile.put("status", user.getStatus());
        profile.put("createdAt", user.getCreatedAt());

        return Result.ok(profile);
    }

    @ApiOperation("我的团队信息（需登录）")
    @GetMapping("/team")
    public Result<TeamResponse> getMyTeam(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userService.getUserById(userId);
        return Result.ok(userService.getTeamInfo(user));
    }

    @ApiOperation("根据地址查询团队信息")
    @GetMapping("/team/{address}")
    public Result<TeamResponse> getTeamByAddress(@PathVariable String address) {
        User user = userService.getUserByAddress(address);
        if (user == null) {
            return Result.ok(null);
        }
        return Result.ok(userService.getTeamInfo(user));
    }

    @ApiOperation("根据地址查询NFT和预售详情")
    @GetMapping("/{address}/nft")
    public Result<AddressNftDetailResponse> getAddressNftDetail(@PathVariable String address) {
        return Result.ok(userService.getAddressNftDetail(address));
    }

    @ApiOperation("根据地址查询下级详情")
    @GetMapping("/{address}/subordinates")
    public Result<List<User>> getSubordinates(@PathVariable String address) {
        return Result.ok(userService.getSubordinates(address));
    }
}
