package com.mengyo.module.system.controller;

import com.mengyo.common.result.Result;
import com.mengyo.module.system.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Tag(name = "文件上传", description = "文件上传相关接口")
@RestController
@RequestMapping("/system/file")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file) {
        String fileUrl = fileUploadService.uploadFile(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("storageType", fileUploadService.getStorageType());
        return Result.success(result);
    }

    @Operation(summary = "上传图片")
    @PostMapping("/upload/image")
    public Result<Map<String, String>> uploadImage(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        String imageUrl = fileUploadService.uploadImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", imageUrl);
        result.put("storageType", fileUploadService.getStorageType());
        return Result.success(result);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(
            @Parameter(description = "文件URL") @RequestParam("url") String fileUrl) {
        boolean success = fileUploadService.deleteFile(fileUrl);
        if (success) {
            return Result.success();
        } else {
            return Result.error("文件删除失败");
        }
    }

    @Operation(summary = "获取当前存储类型")
    @GetMapping("/storage-type")
    public Result<Map<String, String>> getStorageType() {
        Map<String, String> result = new HashMap<>();
        result.put("storageType", fileUploadService.getStorageType());
        return Result.success(result);
    }
}
