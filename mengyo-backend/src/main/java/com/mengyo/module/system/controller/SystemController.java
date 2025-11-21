package com.mengyo.module.system.controller;

import com.mengyo.common.result.Result;
import com.mengyo.module.system.entity.Banner;
import com.mengyo.module.system.entity.SystemConfig;
import com.mengyo.module.system.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统管理控制器
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Tag(name = "系统管理")
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @Operation(summary = "获取轮播图列表")
    @GetMapping("/banners")
    public Result<List<Banner>> getBannerList(@RequestParam(required = false) Integer status) {
        List<Banner> banners = systemService.getBannerList(status);
        return Result.success(banners);
    }

    @Operation(summary = "保存或更新轮播图")
    @PostMapping("/banner")
    public Result<Long> saveBanner(@RequestBody Banner banner) {
        Long bannerId = systemService.saveBanner(banner);
        return Result.success(bannerId);
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/banner/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        systemService.deleteBanner(id);
        return Result.success();
    }

    @Operation(summary = "获取系统配置")
    @GetMapping("/config/{key}")
    public Result<String> getConfigValue(@PathVariable String key) {
        String value = systemService.getConfigValue(key);
        return Result.success(value);
    }

    @Operation(summary = "获取所有配置")
    @GetMapping("/configs")
    public Result<List<SystemConfig>> getAllConfigs() {
        List<SystemConfig> configs = systemService.getAllConfigs();
        return Result.success(configs);
    }

    @Operation(summary = "保存或更新配置")
    @PostMapping("/config")
    public Result<Void> saveConfig(
            @RequestParam String configKey,
            @RequestParam String configValue,
            @RequestParam(required = false) String description) {
        systemService.saveConfig(configKey, configValue, description);
        return Result.success();
    }
}
