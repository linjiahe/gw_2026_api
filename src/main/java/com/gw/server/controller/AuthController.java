package com.gw.server.controller;

import com.gw.server.dto.*;
import com.gw.server.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Api(tags = "认证接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ApiOperation(value = "获取nonce", notes = "根据钱包地址生成签名用的随机nonce，用于后续登录签名验证")
    @PostMapping("/nonce")
    public Result<Map<String, String>> getNonce(@Valid @RequestBody NonceRequest request) {
        String nonce = authService.generateNonce(request.getAddress());
        return Result.ok(Collections.singletonMap("nonce", nonce));
    }

    @ApiOperation(value = "签名登录", notes = "使用钱包签名进行登录。首次登录自动注册并返回isNewUser=true；可选传入inviteCode（邀请人钱包地址）绑定邀请人。返回JWT令牌用于后续鉴权。")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthService.LoginResult result = authService.login(
                request.getAddress(), request.getSignature(), request.getInviteCode()
        );

        LoginResponse response = LoginResponse.builder()
                .token(result.getToken())
                .userId(result.getUserId())
                .address(result.getAddress())
                .isNewUser(result.isNewUser())
                .inviteCode(result.getInviteCode())
                .build();

        return Result.ok(response);
    }
}
