package com.mengyo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    /**
     * 本地存储路径
     */
    private String path;

    /**
     * 访问URL前缀
     */
    private String urlPrefix;

    /**
     * 存储类型：local-本地存储，oss-阿里云OSS
     */
    private String storageType = "local";

    /**
     * 阿里云OSS配置
     */
    private OssConfig oss = new OssConfig();

    @Data
    public static class OssConfig {
        /**
         * 访问密钥ID
         */
        private String accessKeyId;

        /**
         * 访问密钥密码
         */
        private String accessKeySecret;

        /**
         * OSS的Endpoint
         */
        private String endpoint;

        /**
         * Bucket名称
         */
        private String bucketName;

        /**
         * 文件存储目录
         */
        private String directory = "mengyo/";

        /**
         * 访问域名（自定义域名或OSS默认域名）
         */
        private String domain;
    }
}
