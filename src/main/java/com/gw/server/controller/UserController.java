package com.gw.server.controller;

import com.gw.server.dto.AddressNftDetailResponse;
import com.gw.server.dto.Result;
import com.gw.server.dto.TeamResponse;
import com.gw.server.entity.User;
import com.gw.server.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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

    @ApiOperation(value = "获取用户信息", notes = "需在Header中携带Bearer Token。返回当前登录用户的ID、钱包地址、状态和注册时间。")
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

    @ApiOperation(value = "我的团队信息", notes = "需在Header中携带Bearer Token。返回直推地址列表、团队总人数、直推人数、团队NFT总数、直推NFT总数。")
    @GetMapping("/team")
    public Result<TeamResponse> getMyTeam(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userService.getUserById(userId);
        return Result.ok(userService.getTeamInfo(user));
    }

    @ApiOperation(value = "根据地址查询团队信息", notes = "公开接口，无需登录。根据钱包地址查询该用户的团队统计数据。")
    @GetMapping("/team/{address}")
    @ApiImplicitParam(name = "address", value = "钱包地址", required = true, dataType = "string", paramType = "path", example = "0x71C7656EC7ab88b098defB751B7401B5f6d8976F")
    public Result<TeamResponse> getTeamByAddress(@PathVariable String address) {
        User user = userService.getUserByAddress(address);
        if (user == null) {
            return Result.ok(null);
        }
        return Result.ok(userService.getTeamInfo(user));
    }

    @ApiOperation(value = "根据地址查询NFT和预售详情", notes = "公开接口，无需登录。返回该地址下的所有NFT购买记录和预售记录。")
    @GetMapping("/{address}/nft")
    @ApiImplicitParam(name = "address", value = "钱包地址", required = true, dataType = "string", paramType = "path", example = "0x71C7656EC7ab88b098defB751B7401B5f6d8976F")
    public Result<AddressNftDetailResponse> getAddressNftDetail(@PathVariable String address) {
        return Result.ok(userService.getAddressNftDetail(address));
    }

    @ApiOperation(value = "根据地址查询下级详情", notes = "公开接口，无需登录。返回该地址直推的下级用户列表，包含每个下级的ID、地址、状态、邀请码等。")
    @GetMapping("/{address}/subordinates")
    @ApiImplicitParam(name = "address", value = "钱包地址", required = true, dataType = "string", paramType = "path", example = "0x71C7656EC7ab88b098defB751B7401B5f6d8976F")
    public Result<List<User>> getSubordinates(@PathVariable String address) {
        return Result.ok(userService.getSubordinates(address));
    }
}
