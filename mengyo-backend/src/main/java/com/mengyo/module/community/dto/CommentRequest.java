package com.mengyo.module.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 发表评论请求DTO
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Schema(description = "发表评论请求")
public class CommentRequest {

    @Schema(description = "帖子ID", example = "1")
    private Long postId;

    @Schema(description = "父评论ID（回复评论时使用）", example = "0")
    private Long parentId;

    @Schema(description = "回复的用户ID（回复评论时使用）", example = "")
    private Long replyToId;

    @Schema(description = "评论内容", example = "很有帮助，谢谢分享")
    private String content;
}
