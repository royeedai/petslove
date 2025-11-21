package com.mengyo.module.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengyo.module.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论Mapper接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
