package com.mengyo.module.donation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 物资捐赠请求DTO
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Schema(description = "物资捐赠请求")
public class MaterialDonationRequest {

    @Schema(description = "物资名称", example = "猫粮")
    private String materialName;

    @Schema(description = "物资数量", example = "10")
    private Integer materialQuantity;

    @Schema(description = "物资单位", example = "袋")
    private String materialUnit;

    @Schema(description = "目标类型：ANIMAL-动物 TASK-任务 GENERAL-通用", example = "GENERAL")
    private String targetType;

    @Schema(description = "目标ID（指定动物或任务时必填）", example = "1")
    private Long targetId;

    @Schema(description = "是否匿名：0-否 1-是", example = "0")
    private Integer isAnonymous;

    @Schema(description = "留言", example = "希望小动物们健康成长")
    private String message;
}
