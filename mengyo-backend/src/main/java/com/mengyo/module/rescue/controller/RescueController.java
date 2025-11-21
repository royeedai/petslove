package com.mengyo.module.rescue.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mengyo.common.result.Result;
import com.mengyo.module.rescue.dto.AnimalReportRequest;
import com.mengyo.module.rescue.dto.RescueTaskRequest;
import com.mengyo.module.rescue.entity.RescueTask;
import com.mengyo.module.rescue.entity.StrayAnimal;
import com.mengyo.module.rescue.service.RescueTaskService;
import com.mengyo.module.rescue.service.StrayAnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 救助管理控制器
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Tag(name = "救助管理")
@RestController
@RequestMapping("/rescue")
@RequiredArgsConstructor
public class RescueController {

    private final StrayAnimalService strayAnimalService;
    private final RescueTaskService rescueTaskService;

    @Operation(summary = "报备流浪动物")
    @PostMapping("/animal/report")
    public Result<Long> reportAnimal(@RequestHeader("userId") Long userId,
                                     @RequestBody AnimalReportRequest request) {
        Long animalId = strayAnimalService.reportAnimal(userId, request);
        return Result.success(animalId);
    }

    @Operation(summary = "获取流浪动物列表")
    @GetMapping("/animals")
    public Result<Page<StrayAnimal>> getAnimalList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String animalType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String city) {
        Page<StrayAnimal> result = strayAnimalService.getAnimalList(page, size, animalType, status, city);
        return Result.success(result);
    }

    @Operation(summary = "获取动物详情")
    @GetMapping("/animal/{id}")
    public Result<StrayAnimal> getAnimalDetail(@PathVariable Long id) {
        StrayAnimal animal = strayAnimalService.getAnimalDetail(id);
        return Result.success(animal);
    }

    @Operation(summary = "发布救助任务")
    @PostMapping("/task/publish")
    public Result<Long> publishTask(@RequestHeader("userId") Long userId,
                                    @RequestBody RescueTaskRequest request) {
        Long taskId = rescueTaskService.publishTask(userId, request);
        return Result.success(taskId);
    }

    @Operation(summary = "获取救助任务列表")
    @GetMapping("/tasks")
    public Result<Page<RescueTask>> getTaskList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String urgency) {
        Page<RescueTask> result = rescueTaskService.getTaskList(page, size, status, urgency);
        return Result.success(result);
    }

    @Operation(summary = "获取任务详情")
    @GetMapping("/task/{id}")
    public Result<RescueTask> getTaskDetail(@PathVariable Long id) {
        RescueTask task = rescueTaskService.getTaskDetail(id);
        return Result.success(task);
    }

    @Operation(summary = "接受救助任务")
    @PostMapping("/task/{id}/accept")
    public Result<Void> acceptTask(@RequestHeader("userId") Long userId,
                                   @PathVariable Long id) {
        rescueTaskService.acceptTask(userId, id);
        return Result.success();
    }

    @Operation(summary = "完成救助任务")
    @PostMapping("/task/{id}/complete")
    public Result<Void> completeTask(@RequestHeader("userId") Long userId,
                                     @PathVariable Long id,
                                     @RequestParam(required = false) List<String> rescueImages,
                                     @RequestParam(required = false) String rescueNote) {
        rescueTaskService.completeTask(userId, id, rescueImages, rescueNote);
        return Result.success();
    }

    @Operation(summary = "取消救助任务")
    @PostMapping("/task/{id}/cancel")
    public Result<Void> cancelTask(@RequestHeader("userId") Long userId,
                                   @PathVariable Long id) {
        rescueTaskService.cancelTask(userId, id);
        return Result.success();
    }
}
