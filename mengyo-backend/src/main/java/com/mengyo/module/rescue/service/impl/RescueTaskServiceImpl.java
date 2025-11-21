package com.mengyo.module.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mengyo.common.exception.BusinessException;
import com.mengyo.module.rescue.dto.RescueTaskRequest;
import com.mengyo.module.rescue.entity.RescueTask;
import com.mengyo.module.rescue.entity.StrayAnimal;
import com.mengyo.module.rescue.mapper.RescueTaskMapper;
import com.mengyo.module.rescue.service.RescueTaskService;
import com.mengyo.module.rescue.service.StrayAnimalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 救助任务服务实现类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RescueTaskServiceImpl extends ServiceImpl<RescueTaskMapper, RescueTask> implements RescueTaskService {

    private final StrayAnimalService strayAnimalService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishTask(Long userId, RescueTaskRequest request) {
        // 验证动物是否存在
        StrayAnimal animal = strayAnimalService.getById(request.getAnimalId());
        if (animal == null) {
            throw new BusinessException("动物信息不存在");
        }

        RescueTask task = new RescueTask();
        task.setAnimalId(request.getAnimalId());
        task.setPublisherId(userId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUrgency(request.getUrgency());
        task.setStatus("PUBLISHED");

        save(task);
        
        // 更新动物状态为救助中
        strayAnimalService.updateAnimalStatus(request.getAnimalId(), "RESCUING");
        
        log.info("用户{}发布救助任务，ID：{}", userId, task.getId());
        return task.getId();
    }

    @Override
    public Page<RescueTask> getTaskList(Integer page, Integer size, String status, String urgency) {
        Page<RescueTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<RescueTask> wrapper = new LambdaQueryWrapper<>();

        // 条件查询
        if (StringUtils.hasText(status)) {
            wrapper.eq(RescueTask::getStatus, status);
        }
        if (StringUtils.hasText(urgency)) {
            wrapper.eq(RescueTask::getUrgency, urgency);
        }

        // 紧急程度优先，再按创建时间倒序
        wrapper.orderByDesc(RescueTask::getUrgency, RescueTask::getCreateTime);

        return page(pageParam, wrapper);
    }

    @Override
    public RescueTask getTaskDetail(Long id) {
        RescueTask task = getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptTask(Long userId, Long taskId) {
        RescueTask task = getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        if (!"PUBLISHED".equals(task.getStatus())) {
            throw new BusinessException("任务状态不正确，无法接单");
        }

        task.setRescuerId(userId);
        task.setStatus("ACCEPTED");
        task.setAcceptTime(LocalDateTime.now());
        updateById(task);

        log.info("用户{}接受救助任务{}", userId, taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long userId, Long taskId, List<String> rescueImages, String rescueNote) {
        RescueTask task = getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        if (!userId.equals(task.getRescuerId())) {
            throw new BusinessException("只有救助人才能完成任务");
        }

        if (!"ACCEPTED".equals(task.getStatus())) {
            throw new BusinessException("任务状态不正确，无法完成");
        }

        try {
            // 保存救助图片和备注
            if (rescueImages != null && !rescueImages.isEmpty()) {
                task.setRescueImages(objectMapper.writeValueAsString(rescueImages));
            }
            task.setRescueNote(rescueNote);
            task.setStatus("COMPLETED");
            task.setCompleteTime(LocalDateTime.now());
            updateById(task);

            // 更新动物状态为已救助
            strayAnimalService.updateAnimalStatus(task.getAnimalId(), "RESCUED");

            log.info("用户{}完成救助任务{}", userId, taskId);
        } catch (Exception e) {
            log.error("完成救助任务失败", e);
            throw new BusinessException("完成任务失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTask(Long userId, Long taskId) {
        RescueTask task = getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        if (!userId.equals(task.getPublisherId()) && !userId.equals(task.getRescuerId())) {
            throw new BusinessException("无权取消该任务");
        }

        task.setStatus("CANCELLED");
        updateById(task);

        // 更新动物状态回已报备
        strayAnimalService.updateAnimalStatus(task.getAnimalId(), "REPORTED");

        log.info("用户{}取消救助任务{}", userId, taskId);
    }
}
