package com.mengyo.module.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mengyo.module.rescue.dto.RescueTaskRequest;
import com.mengyo.module.rescue.entity.RescueTask;

import java.util.List;

/**
 * 救助任务服务接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public interface RescueTaskService extends IService<RescueTask> {

    /**
     * 发布救助任务
     *
     * @param userId  发布人ID
     * @param request 任务信息
     * @return 任务ID
     */
    Long publishTask(Long userId, RescueTaskRequest request);

    /**
     * 分页查询救助任务列表
     *
     * @param page     当前页
     * @param size     每页数量
     * @param status   状态（可选）
     * @param urgency  紧急程度（可选）
     * @return 分页结果
     */
    Page<RescueTask> getTaskList(Integer page, Integer size, String status, String urgency);

    /**
     * 获取任务详情
     *
     * @param id 任务ID
     * @return 任务详情
     */
    RescueTask getTaskDetail(Long id);

    /**
     * 接受救助任务
     *
     * @param userId 救助人ID
     * @param taskId 任务ID
     */
    void acceptTask(Long userId, Long taskId);

    /**
     * 完成救助任务
     *
     * @param userId       救助人ID
     * @param taskId       任务ID
     * @param rescueImages 救助图片
     * @param rescueNote   救助备注
     */
    void completeTask(Long userId, Long taskId, List<String> rescueImages, String rescueNote);

    /**
     * 取消救助任务
     *
     * @param userId 用户ID
     * @param taskId 任务ID
     */
    void cancelTask(Long userId, Long taskId);
}
