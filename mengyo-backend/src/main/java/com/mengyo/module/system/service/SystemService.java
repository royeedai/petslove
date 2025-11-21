package com.mengyo.module.system.service;

import com.mengyo.module.system.entity.Banner;
import com.mengyo.module.system.entity.SystemConfig;

import java.util.List;

/**
 * 系统服务接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public interface SystemService {

    /**
     * 获取轮播图列表
     *
     * @param status 状态（可选）
     * @return 轮播图列表
     */
    List<Banner> getBannerList(Integer status);

    /**
     * 保存或更新轮播图
     *
     * @param banner 轮播图信息
     * @return 轮播图ID
     */
    Long saveBanner(Banner banner);

    /**
     * 删除轮播图
     *
     * @param id 轮播图ID
     */
    void deleteBanner(Long id);

    /**
     * 获取系统配置
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 获取所有配置
     *
     * @return 配置列表
     */
    List<SystemConfig> getAllConfigs();

    /**
     * 保存或更新配置
     *
     * @param configKey   配置键
     * @param configValue 配置值
     * @param description 描述
     */
    void saveConfig(String configKey, String configValue, String description);
}
