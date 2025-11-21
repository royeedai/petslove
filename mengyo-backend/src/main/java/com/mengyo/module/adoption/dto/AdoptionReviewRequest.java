package com.mengyo.module.adoption.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 领养审核请求DTO
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Schema(description = "领养审核请求")
public class AdoptionReviewRequest {

    @Schema(description = "审核结果：APPROVED-通过 REJECTED-拒绝", example = "APPROVED")
    private String result;

    @Schema(description = "审核备注", example = "申请人条件良好，同意领养")
    private String reviewNote;
}
