package com.mengyo.module.adoption.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mengyo.module.adoption.dto.AdoptionApplyRequest;
import com.mengyo.module.adoption.dto.FollowupRequest;
import com.mengyo.module.adoption.entity.AdoptionApplication;
import com.mengyo.module.adoption.entity.AdoptionFollowup;

import java.util.List;

/**
 * 领养服务接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public interface AdoptionService extends IService<AdoptionApplication> {

    /**
     * 提交领养申请
     *
     * @param userId  申请人ID
     * @param request 申请信息
     * @return 申请ID
     */
    Long submitApplication(Long userId, AdoptionApplyRequest request);

    /**
     * 分页查询领养申请列表
     *
     * @param page   当前页
     * @param size   每页数量
     * @param status 状态（可选）
     * @return 分页结果
     */
    Page<AdoptionApplication> getApplicationList(Integer page, Integer size, String status);

    /**
     * 获取我的领养申请
     *
     * @param userId 用户ID
     * @return 申请列表
     */
    List<AdoptionApplication> getMyApplications(Long userId);

    /**
     * 获取申请详情
     *
     * @param id 申请ID
     * @return 申请详情
     */
    AdoptionApplication getApplicationDetail(Long id);

    /**
     * 审核领养申请
     *
     * @param reviewerId 审核人ID
     * @param applicationId 申请ID
     * @param result 审核结果
     * @param reviewNote 审核备注
     */
    void reviewApplication(Long reviewerId, Long applicationId, String result, String reviewNote);

    /**
     * 添加回访记录
     *
     * @param visitorId 回访人ID
     * @param adoptionId 领养记录ID
     * @param request 回访信息
     * @return 回访记录ID
     */
    Long addFollowup(Long visitorId, Long adoptionId, FollowupRequest request);

    /**
     * 获取回访记录列表
     *
     * @param adoptionId 领养记录ID
     * @return 回访记录列表
     */
    List<AdoptionFollowup> getFollowupList(Long adoptionId);
}
