package com.mengyo.module.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengyo.module.rescue.entity.StrayAnimal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流浪动物Mapper接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Mapper
public interface StrayAnimalMapper extends BaseMapper<StrayAnimal> {
}
