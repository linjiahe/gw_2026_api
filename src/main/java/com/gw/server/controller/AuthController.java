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

    @ApiOperation("获取nonce")
    @PostMapping("/nonce")
    public Result<Map<String, String>> getNonce(@Valid @RequestBody NonceRequest request) {
        String nonce = authService.generateNonce(request.getAddress());
        return Result.ok(Collections.singletonMap("nonce", nonce));
    }

    @ApiOperation("签名登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthService.LoginResult result = authService.login(request.getAddress(), request.getSignature());

        LoginResponse response = LoginResponse.builder()
                .token(result.getToken())
                .userId(result.getUserId())
                .address(result.getAddress())
                .isNewUser(result.isNewUser())
                .build();

        return Result.ok(response);
    }
}
