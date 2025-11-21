package com.mengyo.module.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengyo.module.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知消息Mapper接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
