package com.mengyo.module.donation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 资金捐赠请求DTO
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Schema(description = "资金捐赠请求")
public class MoneyDonationRequest {

    @Schema(description = "捐赠金额", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "目标类型：ANIMAL-动物 TASK-任务 GENERAL-通用", example = "GENERAL")
    private String targetType;

    @Schema(description = "目标ID（指定动物或任务时必填）", example = "1")
    private Long targetId;

    @Schema(description = "支付方式：WECHAT-微信 ALIPAY-支付宝", example = "WECHAT")
    private String paymentMethod;

    @Schema(description = "是否匿名：0-否 1-是", example = "0")
    private Integer isAnonymous;

    @Schema(description = "留言", example = "希望能帮助更多小动物")
    private String message;
}
