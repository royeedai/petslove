package com.mengyo.module.rescue.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 救助任务请求DTO
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Schema(description = "救助任务请求")
public class RescueTaskRequest {

    @Schema(description = "动物ID", example = "1")
    private Long animalId;

    @Schema(description = "任务标题", example = "紧急救助受伤小猫")
    private String title;

    @Schema(description = "任务描述", example = "该猫咪后腿受伤，需要立即送医")
    private String description;

    @Schema(description = "紧急程度：LOW-低 NORMAL-普通 HIGH-高 URGENT-紧急", example = "URGENT")
    private String urgency;
}
