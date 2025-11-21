package com.mengyo.module.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知消息实体类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@TableName("notification")
public class Notification {

    /**
     * 通知ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接收用户ID
     */
    private Long userId;

    /**
     * 通知类型：SYSTEM-系统 RESCUE-救助 ADOPTION-领养 COMMENT-评论 LIKE-点赞
     */
    private String type;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 关联ID
     */
    private Long relatedId;

    /**
     * 关联类型
     */
    private String relatedType;

    /**
     * 是否已读：0-否 1-是
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
