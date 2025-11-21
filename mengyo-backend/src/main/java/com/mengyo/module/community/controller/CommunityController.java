package com.mengyo.module.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mengyo.common.result.Result;
import com.mengyo.module.community.dto.CommentRequest;
import com.mengyo.module.community.dto.PostPublishRequest;
import com.mengyo.module.community.entity.Comment;
import com.mengyo.module.community.entity.Post;
import com.mengyo.module.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区管理控制器
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Tag(name = "社区管理")
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "发布帖子")
    @PostMapping("/post")
    public Result<Long> publishPost(@RequestHeader("userId") Long userId,
                                    @RequestBody PostPublishRequest request) {
        Long postId = communityService.publishPost(userId, request);
        return Result.success(postId);
    }

    @Operation(summary = "获取帖子列表")
    @GetMapping("/posts")
    public Result<Page<Post>> getPostList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category) {
        Page<Post> result = communityService.getPostList(page, size, category);
        return Result.success(result);
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/post/{id}")
    public Result<Post> getPostDetail(@PathVariable Long id) {
        Post post = communityService.getPostDetail(id);
        return Result.success(post);
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/post/{id}")
    public Result<Void> deletePost(@RequestHeader("userId") Long userId,
                                   @PathVariable Long id) {
        communityService.deletePost(userId, id);
        return Result.success();
    }

    @Operation(summary = "发表评论")
    @PostMapping("/comment")
    public Result<Long> publishComment(@RequestHeader("userId") Long userId,
                                       @RequestBody CommentRequest request) {
        Long commentId = communityService.publishComment(userId, request);
        return Result.success(commentId);
    }

    @Operation(summary = "获取评论列表")
    @GetMapping("/post/{id}/comments")
    public Result<List<Comment>> getCommentList(@PathVariable Long id) {
        List<Comment> comments = communityService.getCommentList(id);
        return Result.success(comments);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comment/{id}")
    public Result<Void> deleteComment(@RequestHeader("userId") Long userId,
                                      @PathVariable Long id) {
        communityService.deleteComment(userId, id);
        return Result.success();
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/like")
    public Result<Map<String, Object>> toggleLike(
            @RequestHeader("userId") Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        boolean liked = communityService.toggleLike(userId, targetType, targetId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        return Result.success(result);
    }

    @Operation(summary = "检查是否已点赞")
    @GetMapping("/is-liked")
    public Result<Boolean> isLiked(
            @RequestHeader("userId") Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        boolean liked = communityService.isLiked(userId, targetType, targetId);
        return Result.success(liked);
    }
}
