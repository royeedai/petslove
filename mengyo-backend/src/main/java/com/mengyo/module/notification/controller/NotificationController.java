package com.mengyo.module.notification.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mengyo.common.result.Result;
import com.mengyo.module.notification.entity.Notification;
import com.mengyo.module.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知管理控制器
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Tag(name = "通知管理")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "获取通知列表")
    @GetMapping("/list")
    public Result<Page<Notification>> getNotificationList(
            @RequestHeader("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Notification> result = notificationService.getNotificationList(userId, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(@RequestHeader("userId") Long userId) {
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    @Operation(summary = "标记为已读")
    @PutMapping("/read")
    public Result<Void> markAsRead(
            @RequestHeader("userId") Long userId,
            @RequestParam(required = false) Long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return Result.success();
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(
            @RequestHeader("userId") Long userId,
            @PathVariable Long id) {
        notificationService.deleteNotification(userId, id);
        return Result.success();
    }
}
