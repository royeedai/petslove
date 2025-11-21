package com.mengyo.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mengyo.common.exception.BusinessException;
import com.mengyo.module.community.dto.CommentRequest;
import com.mengyo.module.community.dto.PostPublishRequest;
import com.mengyo.module.community.entity.Comment;
import com.mengyo.module.community.entity.LikeRecord;
import com.mengyo.module.community.entity.Post;
import com.mengyo.module.community.mapper.CommentMapper;
import com.mengyo.module.community.mapper.LikeRecordMapper;
import com.mengyo.module.community.mapper.PostMapper;
import com.mengyo.module.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 社区服务实现类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final LikeRecordMapper likeRecordMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishPost(Long userId, PostPublishRequest request) {
        try {
            Post post = new Post();
            post.setUserId(userId);
            post.setCategory(request.getCategory());
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setVideoUrl(request.getVideoUrl());
            post.setAnimalId(request.getAnimalId());

            // 图片列表转JSON
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                post.setImages(objectMapper.writeValueAsString(request.getImages()));
            }

            post.setViewCount(0);
            post.setLikeCount(0);
            post.setCommentCount(0);
            post.setShareCount(0);
            post.setIsTop(0);
            post.setStatus(1); // 1-正常，可根据需要添加审核机制

            postMapper.insert(post);
            log.info("用户{}发布帖子，ID：{}", userId, post.getId());

            return post.getId();
        } catch (Exception e) {
            log.error("发布帖子失败", e);
            throw new BusinessException("发布帖子失败");
        }
    }

    @Override
    public Page<Post> getPostList(Integer page, Integer size, String category) {
        Page<Post> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

        // 只显示正常状态的帖子
        wrapper.eq(Post::getStatus, 1);

        if (StringUtils.hasText(category)) {
            wrapper.eq(Post::getCategory, category);
        }

        // 置顶帖子优先，再按创建时间倒序
        wrapper.orderByDesc(Post::getIsTop, Post::getCreateTime);

        return postMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post getPostDetail(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() != 1) {
            throw new BusinessException("帖子不存在或已删除");
        }

        // 增加浏览量
        post.setViewCount(post.getViewCount() + 1);
        postMapper.updateById(post);

        return post;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long userId, Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        // 只能删除自己的帖子
        if (!userId.equals(post.getUserId())) {
            throw new BusinessException("无权删除该帖子");
        }

        post.setStatus(0);
        postMapper.updateById(post);
        log.info("用户{}删除帖子{}", userId, postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishComment(Long userId, CommentRequest request) {
        // 验证帖子是否存在
        Post post = postMapper.selectById(request.getPostId());
        if (post == null || post.getStatus() != 1) {
            throw new BusinessException("帖子不存在或已删除");
        }

        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setUserId(userId);
        comment.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        comment.setReplyToId(request.getReplyToId());
        comment.setContent(request.getContent());
        comment.setLikeCount(0);
        comment.setStatus(1);

        commentMapper.insert(comment);

        // 更新帖子评论数
        post.setCommentCount(post.getCommentCount() + 1);
        postMapper.updateById(post);

        log.info("用户{}发表评论，ID：{}", userId, comment.getId());
        return comment.getId();
    }

    @Override
    public List<Comment> getCommentList(Long postId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getStatus, 1)
                .orderByAsc(Comment::getCreateTime);
        return commentMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 只能删除自己的评论
        if (!userId.equals(comment.getUserId())) {
            throw new BusinessException("无权删除该评论");
        }

        comment.setStatus(0);
        commentMapper.updateById(comment);

        // 更新帖子评论数
        Post post = postMapper.selectById(comment.getPostId());
        if (post != null) {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postMapper.updateById(post);
        }

        log.info("用户{}删除评论{}", userId, commentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(Long userId, String targetType, Long targetId) {
        // 检查是否已点赞
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, targetType)
                .eq(LikeRecord::getTargetId, targetId);
        LikeRecord existingRecord = likeRecordMapper.selectOne(wrapper);

        if (existingRecord != null) {
            // 取消点赞
            likeRecordMapper.deleteById(existingRecord.getId());
            updateLikeCount(targetType, targetId, -1);
            log.info("用户{}取消点赞，类型：{}，ID：{}", userId, targetType, targetId);
            return false;
        } else {
            // 点赞
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setTargetType(targetType);
            record.setTargetId(targetId);
            likeRecordMapper.insert(record);
            updateLikeCount(targetType, targetId, 1);
            log.info("用户{}点赞，类型：{}，ID：{}", userId, targetType, targetId);
            return true;
        }
    }

    @Override
    public boolean isLiked(Long userId, String targetType, Long targetId) {
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, targetType)
                .eq(LikeRecord::getTargetId, targetId);
        return likeRecordMapper.selectCount(wrapper) > 0;
    }

    /**
     * 更新点赞数
     */
    private void updateLikeCount(String targetType, Long targetId, int delta) {
        if ("POST".equals(targetType)) {
            Post post = postMapper.selectById(targetId);
            if (post != null) {
                post.setLikeCount(Math.max(0, post.getLikeCount() + delta));
                postMapper.updateById(post);
            }
        } else if ("COMMENT".equals(targetType)) {
            Comment comment = commentMapper.selectById(targetId);
            if (comment != null) {
                comment.setLikeCount(Math.max(0, comment.getLikeCount() + delta));
                commentMapper.updateById(comment);
            }
        }
    }
}
