package com.mengyo.module.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 发布帖子请求DTO
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Schema(description = "发布帖子请求")
public class PostPublishRequest {

    @Schema(description = "分类：KNOWLEDGE-知识分享 DAILY-日常分享 HELP-求助 STORY-故事", example = "DAILY")
    private String category;

    @Schema(description = "标题", example = "我家猫咪的日常")
    private String title;

    @Schema(description = "内容", example = "今天猫咪特别乖...")
    private String content;

    @Schema(description = "图片URL列表")
    private List<String> images;

    @Schema(description = "视频URL", example = "")
    private String videoUrl;

    @Schema(description = "关联动物ID", example = "")
    private Long animalId;
}
