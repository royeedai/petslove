package com.mengyo.module.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mengyo.module.rescue.dto.AnimalReportRequest;
import com.mengyo.module.rescue.entity.StrayAnimal;

/**
 * 流浪动物服务接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public interface StrayAnimalService extends IService<StrayAnimal> {

    /**
     * 报备流浪动物
     *
     * @param userId  报备人ID
     * @param request 报备信息
     * @return 动物ID
     */
    Long reportAnimal(Long userId, AnimalReportRequest request);

    /**
     * 分页查询流浪动物列表
     *
     * @param page       当前页
     * @param size       每页数量
     * @param animalType 动物类型（可选）
     * @param status     状态（可选）
     * @param city       城市（可选）
     * @return 分页结果
     */
    Page<StrayAnimal> getAnimalList(Integer page, Integer size, String animalType, String status, String city);

    /**
     * 获取动物详情
     *
     * @param id 动物ID
     * @return 动物详情
     */
    StrayAnimal getAnimalDetail(Long id);

    /**
     * 更新动物状态
     *
     * @param id     动物ID
     * @param status 新状态
     */
    void updateAnimalStatus(Long id, String status);
}
