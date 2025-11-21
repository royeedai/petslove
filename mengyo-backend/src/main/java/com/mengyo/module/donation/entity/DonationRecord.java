package com.mengyo.module.donation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 捐赠记录实体类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@TableName("donation_record")
public class DonationRecord {

    /**
     * 捐赠ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 捐赠人ID
     */
    private Long donorId;

    /**
     * 捐赠类型：MONEY-资金 MATERIAL-物资
     */
    private String donationType;

    /**
     * 捐赠金额（资金捐赠）
     */
    private BigDecimal amount;

    /**
     * 物资名称（物资捐赠）
     */
    private String materialName;

    /**
     * 物资数量
     */
    private Integer materialQuantity;

    /**
     * 物资单位
     */
    private String materialUnit;

    /**
     * 捐赠目标ID
     */
    private Long targetId;

    /**
     * 目标类型：ANIMAL-动物 TASK-任务 GENERAL-通用
     */
    private String targetType;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付方式：WECHAT-微信 ALIPAY-支付宝
     */
    private String paymentMethod;

    /**
     * 支付状态：PENDING-待支付 SUCCESS-已支付 FAILED-失败 REFUND-已退款
     */
    private String paymentStatus;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 第三方交易号
     */
    private String transactionId;

    /**
     * 是否匿名：0-否 1-是
     */
    private Integer isAnonymous;

    /**
     * 留言
     */
    private String message;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
