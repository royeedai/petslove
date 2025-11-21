package com.mengyo.module.adoption.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 领养申请请求DTO
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Schema(description = "领养申请请求")
public class AdoptionApplyRequest {

    @Schema(description = "动物ID", example = "1")
    private Long animalId;

    @Schema(description = "申请人姓名", example = "张三")
    private String applicantName;

    @Schema(description = "联系电话", example = "13800138000")
    private String applicantPhone;

    @Schema(description = "居住地址", example = "深圳市南山区科技园")
    private String applicantAddress;

    @Schema(description = "住房类型：RENT-租房 OWN-自有", example = "OWN")
    private String housingType;

    @Schema(description = "是否有养宠经验：0-否 1-是", example = "1")
    private Integer hasExperience;

    @Schema(description = "养宠经验描述", example = "养过2年猫咪")
    private String experienceDesc;

    @Schema(description = "家人是否同意：0-否 1-是", example = "1")
    private Integer familyAgree;

    @Schema(description = "领养原因", example = "喜欢小动物，想给它一个温暖的家")
    private String reason;

    @Schema(description = "领养承诺", example = "我承诺会善待它，定期体检，不离不弃")
    private String commitment;
}
