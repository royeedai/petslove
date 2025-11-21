package com.mengyo.module.rescue.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 动物报备请求DTO
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@Schema(description = "动物报备请求")
public class AnimalReportRequest {

    @Schema(description = "动物类型：CAT-猫 DOG-狗 OTHER-其他", example = "CAT")
    private String animalType;

    @Schema(description = "品种", example = "中华田园猫")
    private String breed;

    @Schema(description = "性别：0-未知 1-公 2-母", example = "1")
    private Integer gender;

    @Schema(description = "年龄估计", example = "2个月")
    private String ageEstimate;

    @Schema(description = "毛色", example = "黄白色")
    private String color;

    @Schema(description = "体型：SMALL-小型 MEDIUM-中型 LARGE-大型", example = "SMALL")
    private String size;

    @Schema(description = "健康状况：GOOD-良好 INJURED-受伤 SICK-生病", example = "INJURED")
    private String healthStatus;

    @Schema(description = "详细描述", example = "在小区门口发现，后腿受伤")
    private String description;

    @Schema(description = "图片URL列表")
    private List<String> images;

    @Schema(description = "省", example = "广东省")
    private String locationProvince;

    @Schema(description = "市", example = "深圳市")
    private String locationCity;

    @Schema(description = "区", example = "南山区")
    private String locationDistrict;

    @Schema(description = "详细位置", example = "科技园地铁站A出口")
    private String locationAddress;

    @Schema(description = "纬度", example = "22.5428")
    private BigDecimal locationLat;

    @Schema(description = "经度", example = "113.9434")
    private BigDecimal locationLng;
}
