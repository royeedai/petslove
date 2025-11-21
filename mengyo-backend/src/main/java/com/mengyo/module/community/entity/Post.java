package com.mengyo.module.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子实体类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@TableName("post")
public class Post {

    /**
     * 帖子ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发布人ID
     */
    private Long userId;

    /**
     * 分类：KNOWLEDGE-知识分享 DAILY-日常分享 HELP-求助 STORY-故事
     */
    private String category;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片列表（JSON数组）
     */
    private String images;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 关联动物ID
     */
    private Long animalId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 是否置顶：0-否 1-是
     */
    private Integer isTop;

    /**
     * 状态：0-已删除 1-正常 2-待审核
     */
    private Integer status;

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
