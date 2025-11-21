package com.mengyo.module.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mengyo.common.exception.BusinessException;
import com.mengyo.module.adoption.dto.AdoptionApplyRequest;
import com.mengyo.module.adoption.dto.FollowupRequest;
import com.mengyo.module.adoption.entity.AdoptionApplication;
import com.mengyo.module.adoption.entity.AdoptionFollowup;
import com.mengyo.module.adoption.mapper.AdoptionApplicationMapper;
import com.mengyo.module.adoption.mapper.AdoptionFollowupMapper;
import com.mengyo.module.adoption.service.AdoptionService;
import com.mengyo.module.rescue.entity.StrayAnimal;
import com.mengyo.module.rescue.service.StrayAnimalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 领养服务实现类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdoptionServiceImpl extends ServiceImpl<AdoptionApplicationMapper, AdoptionApplication> implements AdoptionService {

    private final StrayAnimalService strayAnimalService;
    private final AdoptionFollowupMapper followupMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitApplication(Long userId, AdoptionApplyRequest request) {
        // 验证动物是否存在且可领养
        StrayAnimal animal = strayAnimalService.getById(request.getAnimalId());
        if (animal == null) {
            throw new BusinessException("动物信息不存在");
        }
        if (!"RESCUED".equals(animal.getStatus())) {
            throw new BusinessException("该动物当前不可领养");
        }

        // 检查是否已有待审核的申请
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionApplication::getApplicantId, userId)
                .eq(AdoptionApplication::getAnimalId, request.getAnimalId())
                .eq(AdoptionApplication::getStatus, "PENDING");
        if (count(wrapper) > 0) {
            throw new BusinessException("您已经提交过该动物的领养申请，请等待审核");
        }

        AdoptionApplication application = new AdoptionApplication();
        application.setAnimalId(request.getAnimalId());
        application.setApplicantId(userId);
        application.setApplicantName(request.getApplicantName());
        application.setApplicantPhone(request.getApplicantPhone());
        application.setApplicantAddress(request.getApplicantAddress());
        application.setHousingType(request.getHousingType());
        application.setHasExperience(request.getHasExperience());
        application.setExperienceDesc(request.getExperienceDesc());
        application.setFamilyAgree(request.getFamilyAgree());
        application.setReason(request.getReason());
        application.setCommitment(request.getCommitment());
        application.setStatus("PENDING");

        save(application);
        log.info("用户{}提交领养申请，ID：{}", userId, application.getId());

        return application.getId();
    }

    @Override
    public Page<AdoptionApplication> getApplicationList(Integer page, Integer size, String status) {
        Page<AdoptionApplication> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(status)) {
            wrapper.eq(AdoptionApplication::getStatus, status);
        }

        wrapper.orderByDesc(AdoptionApplication::getCreateTime);

        return page(pageParam, wrapper);
    }

    @Override
    public List<AdoptionApplication> getMyApplications(Long userId) {
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionApplication::getApplicantId, userId)
                .orderByDesc(AdoptionApplication::getCreateTime);
        return list(wrapper);
    }

    @Override
    public AdoptionApplication getApplicationDetail(Long id) {
        AdoptionApplication application = getById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        return application;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewApplication(Long reviewerId, Long applicationId, String result, String reviewNote) {
        AdoptionApplication application = getById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        if (!"PENDING".equals(application.getStatus())) {
            throw new BusinessException("申请状态不正确，无法审核");
        }

        application.setReviewerId(reviewerId);
        application.setStatus(result);
        application.setReviewNote(reviewNote);
        application.setReviewTime(LocalDateTime.now());
        updateById(application);

        // 如果审核通过，更新动物状态为已领养
        if ("APPROVED".equals(result)) {
            strayAnimalService.updateAnimalStatus(application.getAnimalId(), "ADOPTED");
        }

        log.info("审核人{}审核领养申请{}，结果：{}", reviewerId, applicationId, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addFollowup(Long visitorId, Long adoptionId, FollowupRequest request) {
        AdoptionApplication application = getById(adoptionId);
        if (application == null) {
            throw new BusinessException("领养记录不存在");
        }

        try {
            AdoptionFollowup followup = new AdoptionFollowup();
            followup.setAdoptionId(adoptionId);
            followup.setVisitorId(visitorId);
            followup.setFollowupDate(request.getFollowupDate());
            followup.setAnimalStatus(request.getAnimalStatus());
            followup.setNote(request.getNote());

            if (request.getImages() != null && !request.getImages().isEmpty()) {
                followup.setImages(objectMapper.writeValueAsString(request.getImages()));
            }

            followupMapper.insert(followup);
            log.info("添加领养回访记录，领养ID：{}，回访ID：{}", adoptionId, followup.getId());

            return followup.getId();
        } catch (Exception e) {
            log.error("添加回访记录失败", e);
            throw new BusinessException("添加回访记录失败");
        }
    }

    @Override
    public List<AdoptionFollowup> getFollowupList(Long adoptionId) {
        LambdaQueryWrapper<AdoptionFollowup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionFollowup::getAdoptionId, adoptionId)
                .orderByDesc(AdoptionFollowup::getFollowupDate);
        return followupMapper.selectList(wrapper);
    }
}
