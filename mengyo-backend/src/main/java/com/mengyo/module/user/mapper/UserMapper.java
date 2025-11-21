package com.mengyo.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengyo.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据openid查询用户
     *
     * @param openid 微信openid
     * @return 用户信息
     */
    User selectByOpenid(@Param("openid") String openid);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhone(@Param("phone") String phone);

    /**
     * 增加用户积分
     *
     * @param userId 用户ID
     * @param points 积分值
     * @return 影响行数
     */
    int addPoints(@Param("userId") Long userId, @Param("points") Integer points);

    /**
     * 扣减用户积分
     *
     * @param userId 用户ID
     * @param points 积分值
     * @return 影响行数
     */
    int deductPoints(@Param("userId") Long userId, @Param("points") Integer points);

}
