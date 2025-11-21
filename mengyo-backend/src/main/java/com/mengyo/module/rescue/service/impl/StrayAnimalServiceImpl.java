package com.mengyo.module.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mengyo.common.exception.BusinessException;
import com.mengyo.module.rescue.dto.AnimalReportRequest;
import com.mengyo.module.rescue.entity.StrayAnimal;
import com.mengyo.module.rescue.mapper.StrayAnimalMapper;
import com.mengyo.module.rescue.service.StrayAnimalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 流浪动物服务实现类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Service
public class StrayAnimalServiceImpl extends ServiceImpl<StrayAnimalMapper, StrayAnimal> implements StrayAnimalService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Long reportAnimal(Long userId, AnimalReportRequest request) {
        try {
            StrayAnimal animal = new StrayAnimal();
            animal.setReportUserId(userId);
            animal.setAnimalType(request.getAnimalType());
            animal.setBreed(request.getBreed());
            animal.setGender(request.getGender());
            animal.setAgeEstimate(request.getAgeEstimate());
            animal.setColor(request.getColor());
            animal.setSize(request.getSize());
            animal.setHealthStatus(request.getHealthStatus());
            animal.setDescription(request.getDescription());
            
            // 图片列表转JSON
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                animal.setImages(objectMapper.writeValueAsString(request.getImages()));
            }
            
            animal.setLocationProvince(request.getLocationProvince());
            animal.setLocationCity(request.getLocationCity());
            animal.setLocationDistrict(request.getLocationDistrict());
            animal.setLocationAddress(request.getLocationAddress());
            animal.setLocationLat(request.getLocationLat());
            animal.setLocationLng(request.getLocationLng());
            animal.setStatus("REPORTED");
            
            save(animal);
            log.info("用户{}报备流浪动物，ID：{}", userId, animal.getId());
            
            return animal.getId();
        } catch (Exception e) {
            log.error("报备流浪动物失败", e);
            throw new BusinessException("报备失败");
        }
    }

    @Override
    public Page<StrayAnimal> getAnimalList(Integer page, Integer size, String animalType, String status, String city) {
        Page<StrayAnimal> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<StrayAnimal> wrapper = new LambdaQueryWrapper<>();
        
        // 条件查询
        if (StringUtils.hasText(animalType)) {
            wrapper.eq(StrayAnimal::getAnimalType, animalType);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(StrayAnimal::getStatus, status);
        }
        if (StringUtils.hasText(city)) {
            wrapper.eq(StrayAnimal::getLocationCity, city);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(StrayAnimal::getCreateTime);
        
        return page(pageParam, wrapper);
    }

    @Override
    public StrayAnimal getAnimalDetail(Long id) {
        StrayAnimal animal = getById(id);
        if (animal == null) {
            throw new BusinessException("动物信息不存在");
        }
        return animal;
    }

    @Override
    public void updateAnimalStatus(Long id, String status) {
        StrayAnimal animal = getById(id);
        if (animal == null) {
            throw new BusinessException("动物信息不存在");
        }
        
        animal.setStatus(status);
        updateById(animal);
        log.info("更新动物{}状态为：{}", id, status);
    }
}
