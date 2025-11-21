package com.mengyo.common.constant;

/**
 * Redis Key常量
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public class RedisKeyConstant {

    /**
     * Redis Key前缀
     */
    private static final String PREFIX = "mengyo:";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = PREFIX + "token:";

    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_PREFIX = PREFIX + "captcha:";

    /**
     * 手机验证码前缀
     */
    public static final String PHONE_CODE_PREFIX = PREFIX + "phone:code:";

    /**
     * 用户信息缓存前缀
     */
    public static final String USER_INFO_PREFIX = PREFIX + "user:info:";

    /**
     * 动物信息缓存前缀
     */
    public static final String ANIMAL_INFO_PREFIX = PREFIX + "animal:info:";

    /**
     * 点赞记录前缀（用户ID+目标类型+目标ID）
     */
    public static final String LIKE_RECORD_PREFIX = PREFIX + "like:";

    /**
     * 浏览量缓存前缀
     */
    public static final String VIEW_COUNT_PREFIX = PREFIX + "view:count:";

    /**
     * 热门帖子缓存
     */
    public static final String HOT_POSTS = PREFIX + "hot:posts";

    /**
     * 今日捐赠排行榜
     */
    public static final String DONATION_RANK_TODAY = PREFIX + "donation:rank:today";

    /**
     * 获取Token完整Key
     */
    public static String getTokenKey(Long userId) {
        return TOKEN_PREFIX + userId;
    }

    /**
     * 获取手机验证码Key
     */
    public static String getPhoneCodeKey(String phone) {
        return PHONE_CODE_PREFIX + phone;
    }

    /**
     * 获取用户信息Key
     */
    public static String getUserInfoKey(Long userId) {
        return USER_INFO_PREFIX + userId;
    }

    /**
     * 获取动物信息Key
     */
    public static String getAnimalInfoKey(Long animalId) {
        return ANIMAL_INFO_PREFIX + animalId;
    }

    /**
     * 获取点赞记录Key
     */
    public static String getLikeRecordKey(Long userId, String targetType, Long targetId) {
        return LIKE_RECORD_PREFIX + userId + ":" + targetType + ":" + targetId;
    }

}
