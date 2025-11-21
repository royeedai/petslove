package com.mengyo.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // ========== 通用状态码 ==========
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    // ========== 认证授权相关 1xxx ==========
    UNAUTHORIZED(1001, "未授权，请先登录"),
    TOKEN_INVALID(1002, "Token无效或已过期"),
    TOKEN_EXPIRED(1003, "Token已过期"),
    PERMISSION_DENIED(1004, "权限不足"),
    ACCOUNT_DISABLED(1005, "账号已被禁用"),
    LOGIN_FAILED(1006, "登录失败"),
    PHONE_CODE_ERROR(1007, "验证码错误"),
    PHONE_CODE_EXPIRED(1008, "验证码已过期"),

    // ========== 用户相关 2xxx ==========
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户已存在"),
    PHONE_ALREADY_EXISTS(2003, "手机号已被注册"),
    USER_INFO_ERROR(2004, "用户信息错误"),
    VERIFY_FAILED(2005, "实名认证失败"),
    ALREADY_VERIFIED(2006, "已完成实名认证"),

    // ========== 动物救助相关 3xxx ==========
    ANIMAL_NOT_FOUND(3001, "动物信息不存在"),
    RESCUE_TASK_NOT_FOUND(3002, "救助任务不存在"),
    RESCUE_TASK_ACCEPTED(3003, "该任务已被接单"),
    CANNOT_ACCEPT_OWN_TASK(3004, "不能接取自己发布的任务"),
    RESCUE_STATUS_ERROR(3005, "救助状态错误"),

    // ========== 领养相关 4xxx ==========
    ADOPTION_APPLICATION_NOT_FOUND(4001, "领养申请不存在"),
    ANIMAL_NOT_AVAILABLE(4002, "该动物暂不可领养"),
    ALREADY_APPLIED(4003, "您已提交过领养申请"),
    APPLICATION_STATUS_ERROR(4004, "申请状态错误"),

    // ========== 捐赠相关 5xxx ==========
    DONATION_NOT_FOUND(5001, "捐赠记录不存在"),
    PAYMENT_FAILED(5002, "支付失败"),
    DONATION_AMOUNT_ERROR(5003, "捐赠金额错误"),

    // ========== 社区相关 6xxx ==========
    POST_NOT_FOUND(6001, "帖子不存在"),
    COMMENT_NOT_FOUND(6002, "评论不存在"),
    CONTENT_ILLEGAL(6003, "内容包含违规信息"),
    ALREADY_LIKED(6004, "您已点赞过了"),

    // ========== 文件相关 7xxx ==========
    FILE_UPLOAD_FAILED(7001, "文件上传失败"),
    FILE_SIZE_EXCEEDED(7002, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(7003, "文件类型不支持"),
    FILE_NOT_FOUND(7004, "文件不存在"),

    // ========== 业务相关 8xxx ==========
    OPERATION_TOO_FREQUENT(8001, "操作过于频繁，请稍后再试"),
    DATA_ALREADY_EXISTS(8002, "数据已存在"),
    CANNOT_DELETE(8003, "无法删除"),
    OPERATION_FAILED(8004, "操作失败"),
    ;

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 返回消息
     */
    private final String message;

}
