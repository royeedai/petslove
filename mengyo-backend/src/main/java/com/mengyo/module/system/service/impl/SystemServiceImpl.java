package com.mengyo.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mengyo.common.exception.BusinessException;
import com.mengyo.module.system.entity.Banner;
import com.mengyo.module.system.entity.SystemConfig;
import com.mengyo.module.system.mapper.BannerMapper;
import com.mengyo.module.system.mapper.SystemConfigMapper;
import com.mengyo.module.system.service.SystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统服务实现类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final BannerMapper bannerMapper;
    private final SystemConfigMapper configMapper;

    @Override
    public List<Banner> getBannerList(Integer status) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Banner::getStatus, status);
        }
        wrapper.orderByAsc(Banner::getSortOrder);
        return bannerMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveBanner(Banner banner) {
        if (banner.getId() != null) {
            // 更新
            bannerMapper.updateById(banner);
            log.info("更新轮播图，ID：{}", banner.getId());
        } else {
            // 新增
            bannerMapper.insert(banner);
            log.info("新增轮播图，ID：{}", banner.getId());
        }
        return banner.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBanner(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }
        bannerMapper.deleteById(id);
        log.info("删除轮播图，ID：{}", id);
    }

    @Override
    public String getConfigValue(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = configMapper.selectOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        return configMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(String configKey, String configValue, String description) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig existingConfig = configMapper.selectOne(wrapper);

        if (existingConfig != null) {
            // 更新
            existingConfig.setConfigValue(configValue);
            if (description != null) {
                existingConfig.setDescription(description);
            }
            configMapper.updateById(existingConfig);
            log.info("更新系统配置，key：{}", configKey);
        } else {
            // 新增
            SystemConfig config = new SystemConfig();
            config.setConfigKey(configKey);
            config.setConfigValue(configValue);
            config.setDescription(description);
            configMapper.insert(config);
            log.info("新增系统配置，key：{}", configKey);
        }
    }
}
