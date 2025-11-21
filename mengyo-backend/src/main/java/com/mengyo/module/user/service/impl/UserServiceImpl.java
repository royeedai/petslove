package com.mengyo.module.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mengyo.common.exception.BusinessException;
import com.mengyo.common.utils.JwtUtil;
import com.mengyo.common.utils.RedisUtil;
import com.mengyo.module.user.dto.*;
import com.mengyo.module.user.entity.User;
import com.mengyo.module.user.mapper.UserMapper;
import com.mengyo.module.user.service.UserPointsRecordService;
import com.mengyo.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserPointsRecordService pointsRecordService;

    @Value("${wechat.appid:}")
    private String wxAppId;

    @Value("${wechat.secret:}")
    private String wxSecret;

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final int SMS_CODE_EXPIRE = 5; // 5分钟过期

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse wxLogin(WxLoginRequest request) {
        // 1. 调用微信接口获取openid
        String openid = getWxOpenid(request.getCode());
        if (StrUtil.isBlank(openid)) {
            throw new BusinessException("微信授权失败");
        }

        // 2. 查询用户是否存在
        User user = userMapper.selectByOpenid(openid);
        
        // 3. 不存在则创建新用户
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname(StrUtil.isNotBlank(request.getNickname()) ? request.getNickname() : "萌友用户");
            user.setAvatar(request.getAvatar());
            user.setGender(request.getGender() != null ? request.getGender() : 0);
            user.setRole("USER");
            user.setPoints(0);
            user.setIsVerified(0);
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            
            save(user);
            log.info("新用户注册成功，用户ID: {}", user.getId());
        } else {
            // 4. 更新用户信息
            if (StrUtil.isNotBlank(request.getNickname())) {
                user.setNickname(request.getNickname());
            }
            if (StrUtil.isNotBlank(request.getAvatar())) {
                user.setAvatar(request.getAvatar());
            }
            if (request.getGender() != null) {
                user.setGender(request.getGender());
            }
            user.setUpdateTime(LocalDateTime.now());
            updateById(user);
        }

        // 5. 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getNickname());

        return new LoginResponse(token, user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse phoneLogin(LoginRequest request) {
        // 1. 验证短信验证码
        if (!verifySmsCode(request.getPhone(), request.getCode())) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 2. 查询用户是否存在
        User user = userMapper.selectByPhone(request.getPhone());
        
        // 3. 不存在则创建新用户
        if (user == null) {
            user = new User();
            user.setPhone(request.getPhone());
            user.setNickname("用户" + request.getPhone().substring(7));
            user.setGender(0);
            user.setRole("USER");
            user.setPoints(0);
            user.setIsVerified(0);
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            
            save(user);
            log.info("新用户注册成功，用户ID: {}, 手机号: {}", user.getId(), user.getPhone());
        }

        // 4. 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getNickname());

        return new LoginResponse(token, user);
    }

    @Override
    public boolean sendSmsCode(String phone) {
        // 校验手机号格式
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }

        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);
        
        // 存储到Redis，5分钟过期
        String key = SMS_CODE_PREFIX + phone;
        redisUtil.set(key, code, SMS_CODE_EXPIRE, TimeUnit.MINUTES);
        
        // TODO: 调用第三方短信服务发送验证码
        log.info("发送短信验证码，手机号: {}, 验证码: {}", phone, code);
        
        return true;
    }

    @Override
    public boolean verifySmsCode(String phone, String code) {
        String key = SMS_CODE_PREFIX + phone;
        String savedCode = (String) redisUtil.get(key);
        
        if (StrUtil.isBlank(savedCode)) {
            return false;
        }
        
        // 验证成功后删除验证码
        if (savedCode.equals(code)) {
            redisUtil.del(key);
            return true;
        }
        
        return false;
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException("用户不存在或已被禁用");
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserInfo(Long userId, UserUpdateRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果要更新手机号，检查手机号是否已被使用
        if (StrUtil.isNotBlank(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            User existUser = userMapper.selectByPhone(request.getPhone());
            if (existUser != null && !existUser.getId().equals(userId)) {
                throw new BusinessException("该手机号已被使用");
            }
        }

        BeanUtils.copyProperties(request, user, "id", "createTime");
        user.setUpdateTime(LocalDateTime.now());
        
        return updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean verifyRealName(Long userId, VerifyRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getIsVerified() == 1) {
            throw new BusinessException("该用户已完成实名认证");
        }

        // TODO: 调用第三方实名认证接口验证身份证信息
        log.info("实名认证，用户ID: {}, 姓名: {}, 身份证: {}", userId, request.getRealName(), request.getIdCard());

        // 更新用户实名信息
        user.setRealName(request.getRealName());
        user.setIdCard(request.getIdCard());
        user.setIsVerified(1);
        user.setUpdateTime(LocalDateTime.now());
        
        return updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPoints(Long userId, Integer points, String type, String description) {
        if (points <= 0) {
            throw new BusinessException("积分值必须大于0");
        }

        // 更新用户积分
        int result = userMapper.addPoints(userId, points);
        if (result <= 0) {
            throw new BusinessException("增加积分失败");
        }

        // 创建积分记录
        pointsRecordService.createRecord(userId, points, type, description);
        
        log.info("用户积分增加，用户ID: {}, 积分: {}, 类型: {}", userId, points, type);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductPoints(Long userId, Integer points, String type, String description) {
        if (points <= 0) {
            throw new BusinessException("积分值必须大于0");
        }

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getPoints() < points) {
            throw new BusinessException("用户积分不足");
        }

        // 扣减用户积分
        int result = userMapper.deductPoints(userId, points);
        if (result <= 0) {
            throw new BusinessException("扣减积分失败");
        }

        // 创建积分记录
        pointsRecordService.createRecord(userId, -points, type, description);
        
        log.info("用户积分扣减，用户ID: {}, 积分: {}, 类型: {}", userId, points, type);
        return true;
    }

    /**
     * 调用微信接口获取openid
     */
    private String getWxOpenid(String code) {
        try {
            String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wxAppId, wxSecret, code
            );
            
            String result = HttpUtil.get(url);
            JSONObject json = JSONUtil.parseObj(result);
            
            if (json.containsKey("openid")) {
                return json.getStr("openid");
            } else {
                log.error("获取微信openid失败: {}", result);
                return null;
            }
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            return null;
        }
    }

}
