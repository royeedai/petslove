package com.mengyo.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mengyo.module.user.dto.*;
import com.mengyo.module.user.entity.User;

/**
 * 用户服务接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public interface UserService extends IService<User> {

    /**
     * 微信授权登录
     *
     * @param request 微信登录请求
     * @return 登录响应
     */
    LoginResponse wxLogin(WxLoginRequest request);

    /**
     * 手机号登录/注册
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse phoneLogin(LoginRequest request);

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return 是否发送成功
     */
    boolean sendSmsCode(String phone);

    /**
     * 验证短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否验证成功
     */
    boolean verifySmsCode(String phone, String code);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId  用户ID
     * @param request 更新请求
     * @return 是否更新成功
     */
    boolean updateUserInfo(Long userId, UserUpdateRequest request);

    /**
     * 实名认证
     *
     * @param userId  用户ID
     * @param request 认证请求
     * @return 是否认证成功
     */
    boolean verifyRealName(Long userId, VerifyRequest request);

    /**
     * 增加用户积分
     *
     * @param userId      用户ID
     * @param points      积分值
     * @param type        积分类型
     * @param description 积分描述
     * @return 是否成功
     */
    boolean addPoints(Long userId, Integer points, String type, String description);

    /**
     * 扣减用户积分
     *
     * @param userId      用户ID
     * @param points      积分值
     * @param type        积分类型
     * @param description 积分描述
     * @return 是否成功
     */
    boolean deductPoints(Long userId, Integer points, String type, String description);

}
