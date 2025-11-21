package com.mengyo.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mengyo.module.user.entity.UserPointsRecord;

import java.util.List;

/**
 * 用户积分记录服务接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public interface UserPointsRecordService extends IService<UserPointsRecord> {

    /**
     * 创建积分记录
     *
     * @param userId      用户ID
     * @param points      积分变动值
     * @param type        积分类型
     * @param description 描述
     * @return 是否成功
     */
    boolean createRecord(Long userId, Integer points, String type, String description);

    /**
     * 获取用户积分记录列表
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 积分记录列表
     */
    List<UserPointsRecord> getUserRecords(Long userId, Integer limit);

}
