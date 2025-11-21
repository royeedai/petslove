package com.mengyo.config;

import cn.hutool.core.util.StrUtil;
import com.mengyo.module.system.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 存储配置加载器
 * 在应用启动时从数据库加载存储配置
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Component
public class StorageConfigLoader implements ApplicationRunner {

    @Autowired
    private SystemService systemService;

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("开始加载存储配置...");

            // 加载存储类型
            String storageType = systemService.getConfigValue("storage_type");
            if (StrUtil.isNotBlank(storageType)) {
                fileUploadConfig.setStorageType(storageType);
                log.info("存储类型: {}", storageType);
            }

            // 如果是OSS存储，加载OSS配置
            if ("oss".equalsIgnoreCase(storageType)) {
                FileUploadConfig.OssConfig ossConfig = fileUploadConfig.getOss();

                String accessKeyId = systemService.getConfigValue("oss_access_key_id");
                if (StrUtil.isNotBlank(accessKeyId)) {
                    ossConfig.setAccessKeyId(accessKeyId);
                }

                String accessKeySecret = systemService.getConfigValue("oss_access_key_secret");
                if (StrUtil.isNotBlank(accessKeySecret)) {
                    ossConfig.setAccessKeySecret(accessKeySecret);
                }

                String endpoint = systemService.getConfigValue("oss_endpoint");
                if (StrUtil.isNotBlank(endpoint)) {
                    ossConfig.setEndpoint(endpoint);
                }

                String bucketName = systemService.getConfigValue("oss_bucket_name");
                if (StrUtil.isNotBlank(bucketName)) {
                    ossConfig.setBucketName(bucketName);
                }

                String domain = systemService.getConfigValue("oss_domain");
                if (StrUtil.isNotBlank(domain)) {
                    ossConfig.setDomain(domain);
                }

                log.info("阿里云OSS配置加载完成");
            }

            log.info("存储配置加载完成");
        } catch (Exception e) {
            log.warn("加载存储配置失败，使用默认配置", e);
        }
    }
}
