package com.mengyo.module.adoption.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 领养回访记录实体类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@TableName("adoption_followup")
public class AdoptionFollowup {

    /**
     * 回访ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 领养记录ID
     */
    private Long adoptionId;

    /**
     * 回访人ID
     */
    private Long visitorId;

    /**
     * 回访日期
     */
    private LocalDate followupDate;

    /**
     * 动物状况：GOOD-良好 NORMAL-一般 BAD-不佳
     */
    private String animalStatus;

    /**
     * 回访图片（JSON数组）
     */
    private String images;

    /**
     * 回访记录
     */
    private String note;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
