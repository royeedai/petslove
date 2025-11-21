package com.mengyo.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mengyo.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
@Schema(description = "用户实体")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 微信openid
     */
    @Schema(description = "微信openid")
    private String openid;

    /**
     * 微信unionid
     */
    @Schema(description = "微信unionid")
    private String unionid;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;

    /**
     * 性别 0-未知 1-男 2-女
     */
    @Schema(description = "性别 0-未知 1-男 2-女")
    private Integer gender;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    private String idCard;

    /**
     * 是否实名认证 0-否 1-是
     */
    @Schema(description = "是否实名认证 0-否 1-是")
    private Integer isVerified;

    /**
     * 角色 USER-普通用户 VOLUNTEER-志愿者 ADMIN-管理员
     */
    @Schema(description = "角色")
    private String role;

    /**
     * 积分
     */
    @Schema(description = "积分")
    private Integer points;

    /**
     * 状态 0-禁用 1-正常
     */
    @Schema(description = "状态 0-禁用 1-正常")
    private Integer status;

}
