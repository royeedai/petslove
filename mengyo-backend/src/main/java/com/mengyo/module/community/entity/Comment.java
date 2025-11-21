package com.mengyo.module.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@TableName("comment")
public class Comment {

    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 评论人ID
     */
    private Long userId;

    /**
     * 父评论ID，0表示一级评论
     */
    private Long parentId;

    /**
     * 回复的用户ID
     */
    private Long replyToId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态：0-已删除 1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
