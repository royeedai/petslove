package com.mengyo.module.system.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public interface FileUploadService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file);

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);

    /**
     * 获取当前存储类型
     *
     * @return 存储类型
     */
    String getStorageType();
}
