package com.mengyo.module.adoption.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mengyo.common.result.Result;
import com.mengyo.module.adoption.dto.AdoptionApplyRequest;
import com.mengyo.module.adoption.dto.AdoptionReviewRequest;
import com.mengyo.module.adoption.dto.FollowupRequest;
import com.mengyo.module.adoption.entity.AdoptionApplication;
import com.mengyo.module.adoption.entity.AdoptionFollowup;
import com.mengyo.module.adoption.service.AdoptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 领养管理控制器
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Tag(name = "领养管理")
@RestController
@RequestMapping("/adoption")
@RequiredArgsConstructor
public class AdoptionController {

    private final AdoptionService adoptionService;

    @Operation(summary = "提交领养申请")
    @PostMapping("/apply")
    public Result<Long> submitApplication(@RequestHeader("userId") Long userId,
                                          @RequestBody AdoptionApplyRequest request) {
        Long applicationId = adoptionService.submitApplication(userId, request);
        return Result.success(applicationId);
    }

    @Operation(summary = "获取领养申请列表")
    @GetMapping("/applications")
    public Result<Page<AdoptionApplication>> getApplicationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        Page<AdoptionApplication> result = adoptionService.getApplicationList(page, size, status);
        return Result.success(result);
    }

    @Operation(summary = "获取我的领养申请")
    @GetMapping("/my-applications")
    public Result<List<AdoptionApplication>> getMyApplications(@RequestHeader("userId") Long userId) {
        List<AdoptionApplication> applications = adoptionService.getMyApplications(userId);
        return Result.success(applications);
    }

    @Operation(summary = "获取申请详情")
    @GetMapping("/application/{id}")
    public Result<AdoptionApplication> getApplicationDetail(@PathVariable Long id) {
        AdoptionApplication application = adoptionService.getApplicationDetail(id);
        return Result.success(application);
    }

    @Operation(summary = "审核领养申请")
    @PostMapping("/application/{id}/review")
    public Result<Void> reviewApplication(@RequestHeader("userId") Long userId,
                                          @PathVariable Long id,
                                          @RequestBody AdoptionReviewRequest request) {
        adoptionService.reviewApplication(userId, id, request.getResult(), request.getReviewNote());
        return Result.success();
    }

    @Operation(summary = "添加回访记录")
    @PostMapping("/application/{id}/followup")
    public Result<Long> addFollowup(@RequestHeader("userId") Long userId,
                                    @PathVariable Long id,
                                    @RequestBody FollowupRequest request) {
        Long followupId = adoptionService.addFollowup(userId, id, request);
        return Result.success(followupId);
    }

    @Operation(summary = "获取回访记录列表")
    @GetMapping("/application/{id}/followups")
    public Result<List<AdoptionFollowup>> getFollowupList(@PathVariable Long id) {
        List<AdoptionFollowup> followups = adoptionService.getFollowupList(id);
        return Result.success(followups);
    }
}
