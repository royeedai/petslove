package com.mengyo.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mengyo.module.community.dto.CommentRequest;
import com.mengyo.module.community.dto.PostPublishRequest;
import com.mengyo.module.community.entity.Comment;
import com.mengyo.module.community.entity.Post;

import java.util.List;

/**
 * 社区服务接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public interface CommunityService {

    /**
     * 发布帖子
     *
     * @param userId  发布人ID
     * @param request 帖子信息
     * @return 帖子ID
     */
    Long publishPost(Long userId, PostPublishRequest request);

    /**
     * 分页查询帖子列表
     *
     * @param page     当前页
     * @param size     每页数量
     * @param category 分类（可选）
     * @return 分页结果
     */
    Page<Post> getPostList(Integer page, Integer size, String category);

    /**
     * 获取帖子详情
     *
     * @param postId 帖子ID
     * @return 帖子详情
     */
    Post getPostDetail(Long postId);

    /**
     * 删除帖子
     *
     * @param userId 用户ID
     * @param postId 帖子ID
     */
    void deletePost(Long userId, Long postId);

    /**
     * 发表评论
     *
     * @param userId  评论人ID
     * @param request 评论信息
     * @return 评论ID
     */
    Long publishComment(Long userId, CommentRequest request);

    /**
     * 获取帖子的评论列表
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<Comment> getCommentList(Long postId);

    /**
     * 删除评论
     *
     * @param userId    用户ID
     * @param commentId 评论ID
     */
    void deleteComment(Long userId, Long commentId);

    /**
     * 点赞/取消点赞
     *
     * @param userId     用户ID
     * @param targetType 目标类型：POST-帖子 COMMENT-评论
     * @param targetId   目标ID
     * @return 是否点赞成功（true-点赞，false-取消点赞）
     */
    boolean toggleLike(Long userId, String targetType, Long targetId);

    /**
     * 检查是否已点赞
     *
     * @param userId     用户ID
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @return 是否已点赞
     */
    boolean isLiked(Long userId, String targetType, Long targetId);
}
