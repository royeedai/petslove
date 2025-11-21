package com.mengyo.module.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户积分记录实体类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Data
@TableName("user_points_record")
@Schema(description = "用户积分记录实体")
public class UserPointsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 积分变动值（正数为增加，负数为减少）
     */
    @Schema(description = "积分变动值")
    private Integer points;

    /**
     * 类型
     */
    @Schema(description = "类型 SIGN-签到 RESCUE-救助 ADOPTION-领养 DONATION-捐赠 POST-发帖")
    private String type;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
