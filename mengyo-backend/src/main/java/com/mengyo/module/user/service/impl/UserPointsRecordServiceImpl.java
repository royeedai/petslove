package com.mengyo.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mengyo.module.user.entity.UserPointsRecord;
import com.mengyo.module.user.mapper.UserPointsRecordMapper;
import com.mengyo.module.user.service.UserPointsRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户积分记录服务实现类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Service
public class UserPointsRecordServiceImpl extends ServiceImpl<UserPointsRecordMapper, UserPointsRecord> 
        implements UserPointsRecordService {

    @Override
    public boolean createRecord(Long userId, Integer points, String type, String description) {
        UserPointsRecord record = new UserPointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(type);
        record.setDescription(description);
        record.setCreateTime(LocalDateTime.now());
        
        return save(record);
    }

    @Override
    public List<UserPointsRecord> getUserRecords(Long userId, Integer limit) {
        LambdaQueryWrapper<UserPointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPointsRecord::getUserId, userId)
                .orderByDesc(UserPointsRecord::getCreateTime);
        
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        
        return list(wrapper);
    }

}
