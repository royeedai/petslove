package com.mengyo.module.donation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengyo.module.donation.entity.DonationRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 捐赠记录Mapper接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Mapper
public interface DonationRecordMapper extends BaseMapper<DonationRecord> {
}
