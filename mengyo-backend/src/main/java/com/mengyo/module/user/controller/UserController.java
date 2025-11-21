package com.mengyo.module.user.controller;

import com.mengyo.common.result.Result;
import com.mengyo.module.user.dto.*;
import com.mengyo.module.user.entity.User;
import com.mengyo.module.user.entity.UserPointsRecord;
import com.mengyo.module.user.service.UserPointsRecordService;
import com.mengyo.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserPointsRecordService pointsRecordService;

    @PostMapping("/wx-login")
    @Operation(summary = "微信授权登录")
    public Result<LoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        LoginResponse response = userService.wxLogin(request);
        return Result.success(response);
    }

    @PostMapping("/phone-login")
    @Operation(summary = "手机号登录/注册")
    public Result<LoginResponse> phoneLogin(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.phoneLogin(request);
        return Result.success(response);
    }

    @PostMapping("/send-code")
    @Operation(summary = "发送短信验证码")
    public Result<Void> sendSmsCode(
            @Parameter(description = "手机号", required = true)
            @NotBlank(message = "手机号不能为空")
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
            @RequestParam String phone) {
        userService.sendSmsCode(phone);
        return Result.success("验证码已发送");
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public Result<User> getUserInfo(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId) {
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    @PutMapping("/info")
    @Operation(summary = "更新用户信息")
    public Result<Void> updateUserInfo(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUserInfo(userId, request);
        return Result.success("更新成功");
    }

    @PostMapping("/verify")
    @Operation(summary = "实名认证")
    public Result<Void> verifyRealName(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Valid @RequestBody VerifyRequest request) {
        userService.verifyRealName(userId, request);
        return Result.success("实名认证成功");
    }

    @GetMapping("/points/records")
    @Operation(summary = "获取积分记录")
    public Result<List<UserPointsRecord>> getPointsRecords(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "限制数量")
            @RequestParam(defaultValue = "20") Integer limit) {
        List<UserPointsRecord> records = pointsRecordService.getUserRecords(userId, limit);
        return Result.success(records);
    }

    @PostMapping("/points/add")
    @Operation(summary = "增加用户积分")
    public Result<Void> addPoints(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "积分值", required = true)
            @RequestParam Integer points,
            @Parameter(description = "积分类型", required = true)
            @RequestParam String type,
            @Parameter(description = "积分描述")
            @RequestParam(required = false) String description) {
        userService.addPoints(userId, points, type, description);
        return Result.success("积分增加成功");
    }

    @PostMapping("/points/deduct")
    @Operation(summary = "扣减用户积分")
    public Result<Void> deductPoints(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "积分值", required = true)
            @RequestParam Integer points,
            @Parameter(description = "积分类型", required = true)
            @RequestParam String type,
            @Parameter(description = "积分描述")
            @RequestParam(required = false) String description) {
        userService.deductPoints(userId, points, type, description);
        return Result.success("积分扣减成功");
    }

}
