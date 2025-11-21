package com.mengyo.module.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengyo.module.adoption.entity.AdoptionApplication;
import org.apache.ibatis.annotations.Mapper;

/**
 * 领养申请Mapper接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Mapper
public interface AdoptionApplicationMapper extends BaseMapper<AdoptionApplication> {
}
