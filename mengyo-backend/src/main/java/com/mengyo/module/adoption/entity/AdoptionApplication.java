package com.mengyo.module.adoption.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 领养申请实体类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@TableName("adoption_application")
public class AdoptionApplication {

    /**
     * 申请ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 动物ID
     */
    private Long animalId;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 联系电话
     */
    private String applicantPhone;

    /**
     * 居住地址
     */
    private String applicantAddress;

    /**
     * 住房类型：RENT-租房 OWN-自有
     */
    private String housingType;

    /**
     * 是否有养宠经验：0-否 1-是
     */
    private Integer hasExperience;

    /**
     * 养宠经验描述
     */
    private String experienceDesc;

    /**
     * 家人是否同意：0-否 1-是
     */
    private Integer familyAgree;

    /**
     * 领养原因
     */
    private String reason;

    /**
     * 领养承诺
     */
    private String commitment;

    /**
     * 状态：PENDING-待审核 APPROVED-已通过 REJECTED-已拒绝 COMPLETED-已完成
     */
    private String status;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 审核备注
     */
    private String reviewNote;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

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
