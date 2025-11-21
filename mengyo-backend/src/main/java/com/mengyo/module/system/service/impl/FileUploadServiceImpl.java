package com.mengyo.module.system.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.mengyo.common.exception.BusinessException;
import com.mengyo.config.FileUploadConfig;
import com.mengyo.module.system.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传服务实现
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    /**
     * 允许上传的图片格式
     */
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    /**
     * 允许上传的文件格式
     */
    private static final List<String> FILE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp",  // 图片
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",  // 文档
            "zip", "rar", "7z",  // 压缩包
            "mp4", "avi", "mov"  // 视频
    );

    /**
     * 最大文件大小：10MB
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Override
    public String uploadFile(MultipartFile file) {
        // 校验文件
        validateFile(file, FILE_EXTENSIONS);

        // 根据存储类型选择上传方式
        String storageType = fileUploadConfig.getStorageType();
        if ("oss".equalsIgnoreCase(storageType)) {
            return uploadToOss(file);
        } else {
            return uploadToLocal(file);
        }
    }

    @Override
    public String uploadImage(MultipartFile file) {
        // 校验图片
        validateFile(file, IMAGE_EXTENSIONS);

        // 根据存储类型选择上传方式
        String storageType = fileUploadConfig.getStorageType();
        if ("oss".equalsIgnoreCase(storageType)) {
            return uploadToOss(file);
        } else {
            return uploadToLocal(file);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (StrUtil.isBlank(fileUrl)) {
            return false;
        }

        try {
            String storageType = fileUploadConfig.getStorageType();
            if ("oss".equalsIgnoreCase(storageType)) {
                return deleteFromOss(fileUrl);
            } else {
                return deleteFromLocal(fileUrl);
            }
        } catch (Exception e) {
            log.error("删除文件失败: {}", fileUrl, e);
            return false;
        }
    }

    @Override
    public String getStorageType() {
        return fileUploadConfig.getStorageType();
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file, List<String> allowedExtensions) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过10MB");
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new BusinessException("文件名不能为空");
        }

        String extension = FileUtil.extName(originalFilename).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            throw new BusinessException("不支持的文件格式: " + extension);
        }
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(String originalFilename) {
        // 按日期分目录: 2025/11/21/uuid.jpg
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String extension = FileUtil.extName(originalFilename);
        String filename = IdUtil.simpleUUID() + "." + extension;
        return date + "/" + filename;
    }

    /**
     * 上传到本地
     */
    private String uploadToLocal(MultipartFile file) {
        try {
            String filePath = generateFilePath(file.getOriginalFilename());
            String fullPath = fileUploadConfig.getPath() + filePath;

            // 创建目录
            File targetFile = new File(fullPath);
            FileUtil.mkParentDirs(targetFile);

            // 保存文件
            file.transferTo(targetFile);

            // 返回访问URL
            return fileUploadConfig.getUrlPrefix() + filePath;
        } catch (IOException e) {
            log.error("本地文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }
    }

    /**
     * 上传到阿里云OSS
     */
    private String uploadToOss(MultipartFile file) {
        FileUploadConfig.OssConfig ossConfig = fileUploadConfig.getOss();

        // 校验OSS配置
        if (StrUtil.isBlank(ossConfig.getAccessKeyId()) ||
                StrUtil.isBlank(ossConfig.getAccessKeySecret()) ||
                StrUtil.isBlank(ossConfig.getEndpoint()) ||
                StrUtil.isBlank(ossConfig.getBucketName())) {
            throw new BusinessException("阿里云OSS配置不完整");
        }

        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(
                    ossConfig.getEndpoint(),
                    ossConfig.getAccessKeyId(),
                    ossConfig.getAccessKeySecret()
            );

            // 生成文件路径
            String filePath = generateFilePath(file.getOriginalFilename());
            String objectName = ossConfig.getDirectory() + filePath;

            // 上传文件
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(),
                    objectName,
                    inputStream
            );
            ossClient.putObject(putObjectRequest);

            // 返回访问URL
            String domain = ossConfig.getDomain();
            if (StrUtil.isBlank(domain)) {
                // 使用默认域名
                domain = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint();
            }
            return domain + "/" + objectName;

        } catch (Exception e) {
            log.error("OSS文件上传失败", e);
            throw new BusinessException("文件上传失败");
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从本地删除文件
     */
    private boolean deleteFromLocal(String fileUrl) {
        try {
            // 从URL中提取文件路径
            String urlPrefix = fileUploadConfig.getUrlPrefix();
            if (!fileUrl.startsWith(urlPrefix)) {
                return false;
            }

            String filePath = fileUrl.substring(urlPrefix.length());
            String fullPath = fileUploadConfig.getPath() + filePath;

            File file = new File(fullPath);
            if (file.exists()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            log.error("删除本地文件失败: {}", fileUrl, e);
            return false;
        }
    }

    /**
     * 从OSS删除文件
     */
    private boolean deleteFromOss(String fileUrl) {
        FileUploadConfig.OssConfig ossConfig = fileUploadConfig.getOss();

        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(
                    ossConfig.getEndpoint(),
                    ossConfig.getAccessKeyId(),
                    ossConfig.getAccessKeySecret()
            );

            // 从URL中提取object名称
            String domain = ossConfig.getDomain();
            if (StrUtil.isBlank(domain)) {
                domain = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint();
            }

            if (!fileUrl.startsWith(domain)) {
                return false;
            }

            String objectName = fileUrl.substring(domain.length() + 1);
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
            return true;

        } catch (Exception e) {
            log.error("删除OSS文件失败: {}", fileUrl, e);
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
