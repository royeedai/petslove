package com.mengyo.module.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 流浪动物实体类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@TableName("stray_animal")
public class StrayAnimal {

    /**
     * 动物ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报备人ID
     */
    private Long reportUserId;

    /**
     * 动物类型：CAT-猫 DOG-狗 OTHER-其他
     */
    private String animalType;

    /**
     * 品种
     */
    private String breed;

    /**
     * 性别：1-公 2-母 0-未知
     */
    private Integer gender;

    /**
     * 年龄估计
     */
    private String ageEstimate;

    /**
     * 毛色
     */
    private String color;

    /**
     * 体型：SMALL-小型 MEDIUM-中型 LARGE-大型
     */
    private String size;

    /**
     * 健康状况：GOOD-良好 INJURED-受伤 SICK-生病
     */
    private String healthStatus;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 图片URL列表（JSON数组）
     */
    private String images;

    /**
     * 省
     */
    private String locationProvince;

    /**
     * 市
     */
    private String locationCity;

    /**
     * 区
     */
    private String locationDistrict;

    /**
     * 详细位置
     */
    private String locationAddress;

    /**
     * 纬度
     */
    private BigDecimal locationLat;

    /**
     * 经度
     */
    private BigDecimal locationLng;

    /**
     * 状态：REPORTED-已报备 RESCUING-救助中 RESCUED-已救助 ADOPTED-已领养 DEAD-已死亡
     */
    private String status;

    /**
     * 关联救助记录ID
     */
    private Long rescueId;

    /**
     * 关联领养记录ID
     */
    private Long adoptionId;

    /**
     * 是否删除：0-否 1-是
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
