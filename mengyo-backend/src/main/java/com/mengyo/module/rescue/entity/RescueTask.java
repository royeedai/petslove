package com.mengyo.module.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 救助任务实体类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@TableName("rescue_task")
public class RescueTask {

    /**
     * 任务ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 动物ID
     */
    private Long animalId;

    /**
     * 发布人ID
     */
    private Long publisherId;

    /**
     * 救助人ID
     */
    private Long rescuerId;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 紧急程度：LOW-低 NORMAL-普通 HIGH-高 URGENT-紧急
     */
    private String urgency;

    /**
     * 状态：PUBLISHED-已发布 ACCEPTED-已接单 COMPLETED-已完成 CANCELLED-已取消
     */
    private String status;

    /**
     * 接单时间
     */
    private LocalDateTime acceptTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 救助过程图片（JSON数组）
     */
    private String rescueImages;

    /**
     * 救助备注
     */
    private String rescueNote;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDeleted;

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
