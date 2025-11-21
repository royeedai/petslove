package com.mengyo.module.adoption.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 回访记录请求DTO
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Schema(description = "回访记录请求")
public class FollowupRequest {

    @Schema(description = "回访日期", example = "2025-11-21")
    private LocalDate followupDate;

    @Schema(description = "动物状况：GOOD-良好 NORMAL-一般 BAD-不佳", example = "GOOD")
    private String animalStatus;

    @Schema(description = "回访图片列表")
    private List<String> images;

    @Schema(description = "回访记录", example = "动物健康状况良好，适应新环境")
    private String note;
}
