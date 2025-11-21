package com.mengyo.module.notification.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mengyo.module.notification.entity.Notification;

/**
 * 通知服务接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 发送通知
     *
     * @param userId      接收用户ID
     * @param type        通知类型
     * @param title       标题
     * @param content     内容
     * @param relatedId   关联ID
     * @param relatedType 关联类型
     */
    void sendNotification(Long userId, String type, String title, String content, Long relatedId, String relatedType);

    /**
     * 分页查询通知列表
     *
     * @param userId 用户ID
     * @param page   当前页
     * @param size   每页数量
     * @return 分页结果
     */
    Page<Notification> getNotificationList(Long userId, Integer page, Integer size);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记为已读
     *
     * @param userId         用户ID
     * @param notificationId 通知ID（为空时标记所有为已读）
     */
    void markAsRead(Long userId, Long notificationId);

    /**
     * 删除通知
     *
     * @param userId         用户ID
     * @param notificationId 通知ID
     */
    void deleteNotification(Long userId, Long notificationId);
}
