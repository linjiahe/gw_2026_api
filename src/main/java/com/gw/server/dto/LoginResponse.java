package com.gw.server.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("登录响应")
public class LoginResponse {

    @ApiModelProperty(value = "JWT令牌，后续请求需放入Header", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @ApiModelProperty(value = "是否首次登录", example = "true")
    private boolean isNewUser;

    @ApiModelProperty(value = "用户ID", example = "1")
    private Integer userId;

    @ApiModelProperty(value = "钱包地址（小写）", example = "0x71c7656ec7ab88b098defb751b7401b5f6d8976f")
    private String address;

    @ApiModelProperty(value = "用户自己的邀请码", example = "K3MNPQR7")
    private String inviteCode;
}
