package com.mengyo.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求DTO
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Schema(description = "微信登录请求")
public class WxLoginRequest {

    @NotBlank(message = "微信授权码不能为空")
    @Schema(description = "微信授权码", example = "001abc2xyz")
    private String code;

    @Schema(description = "用户昵称", example = "萌友用户")
    private String nickname;

    @Schema(description = "用户头像", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "性别 0-未知 1-男 2-女", example = "0")
    private Integer gender;

}
