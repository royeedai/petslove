package com.mengyo.module.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mengyo.common.exception.BusinessException;
import com.mengyo.module.notification.entity.Notification;
import com.mengyo.module.notification.mapper.NotificationMapper;
import com.mengyo.module.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知服务实现类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotification(Long userId, String type, String title, String content, Long relatedId, String relatedType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setRelatedType(relatedType);
        notification.setIsRead(0);

        save(notification);
        log.info("发送通知给用户{}，类型：{}，内容：{}", userId, type, content);
    }

    @Override
    public Page<Notification> getNotificationList(Long userId, Integer page, Integer size) {
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreateTime);
        return page(pageParam, wrapper);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        return count(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long userId, Long notificationId) {
        if (notificationId != null) {
            // 标记指定通知为已读
            Notification notification = getById(notificationId);
            if (notification == null) {
                throw new BusinessException("通知不存在");
            }
            if (!userId.equals(notification.getUserId())) {
                throw new BusinessException("无权操作该通知");
            }
            notification.setIsRead(1);
            updateById(notification);
        } else {
            // 标记所有通知为已读
            LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Notification::getUserId, userId)
                    .eq(Notification::getIsRead, 0)
                    .set(Notification::getIsRead, 1);
            update(wrapper);
        }
        log.info("用户{}标记通知为已读", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = getById(notificationId);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }
        if (!userId.equals(notification.getUserId())) {
            throw new BusinessException("无权删除该通知");
        }
        removeById(notificationId);
        log.info("用户{}删除通知{}", userId, notificationId);
    }
}
