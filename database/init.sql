-- ==========================================
-- 萌友益站 - 数据库初始化脚本
-- 数据库版本: MySQL 8.0
-- 创建日期: 2025-11-21
-- ==========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `mengyo_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `mengyo_db`;

-- ==========================================
-- 用户相关表
-- ==========================================

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `openid` VARCHAR(64) UNIQUE COMMENT '微信openid',
  `unionid` VARCHAR(64) COMMENT '微信unionid',
  `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `avatar` VARCHAR(255) COMMENT '头像',
  `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `real_name` VARCHAR(20) COMMENT '真实姓名',
  `id_card` VARCHAR(18) COMMENT '身份证号',
  `is_verified` TINYINT DEFAULT 0 COMMENT '是否实名认证 0-否 1-是',
  `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色 USER-普通用户 VOLUNTEER-志愿者 ADMIN-管理员',
  `points` INT DEFAULT 0 COMMENT '积分',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_phone` (`phone`),
  INDEX `idx_openid` (`openid`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户地址表
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `contact_name` VARCHAR(20) COMMENT '联系人姓名',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `province` VARCHAR(50) COMMENT '省',
  `city` VARCHAR(50) COMMENT '市',
  `district` VARCHAR(50) COMMENT '区',
  `address` VARCHAR(200) COMMENT '详细地址',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认地址 0-否 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 用户积分记录表
DROP TABLE IF EXISTS `user_points_record`;
CREATE TABLE `user_points_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `points` INT NOT NULL COMMENT '积分变动值（正数为增加，负数为减少）',
  `type` VARCHAR(20) COMMENT '类型 SIGN-签到 RESCUE-救助 ADOPTION-领养 DONATION-捐赠 POST-发帖',
  `description` VARCHAR(200) COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户积分记录表';

-- ==========================================
-- 动物相关表
-- ==========================================

-- 流浪动物表
DROP TABLE IF EXISTS `stray_animal`;
CREATE TABLE `stray_animal` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '动物ID',
  `report_user_id` BIGINT NOT NULL COMMENT '报备人ID',
  `animal_type` VARCHAR(20) COMMENT '动物类型 CAT-猫 DOG-狗 OTHER-其他',
  `breed` VARCHAR(50) COMMENT '品种',
  `gender` TINYINT COMMENT '性别 1-公 2-母 0-未知',
  `age_estimate` VARCHAR(20) COMMENT '年龄估计（如：2个月、1岁等）',
  `color` VARCHAR(50) COMMENT '毛色',
  `size` VARCHAR(20) COMMENT '体型 SMALL-小型 MEDIUM-中型 LARGE-大型',
  `health_status` VARCHAR(20) COMMENT '健康状况 GOOD-良好 INJURED-受伤 SICK-生病',
  `description` TEXT COMMENT '详细描述',
  `images` TEXT COMMENT '图片URL列表，JSON数组格式',
  `location_province` VARCHAR(50) COMMENT '省',
  `location_city` VARCHAR(50) COMMENT '市',
  `location_district` VARCHAR(50) COMMENT '区',
  `location_address` VARCHAR(200) COMMENT '详细位置',
  `location_lat` DECIMAL(10,7) COMMENT '纬度',
  `location_lng` DECIMAL(10,7) COMMENT '经度',
  `status` VARCHAR(20) DEFAULT 'REPORTED' COMMENT '状态 REPORTED-已报备 RESCUING-救助中 RESCUED-已救助 ADOPTED-已领养 DEAD-已死亡',
  `rescue_id` BIGINT COMMENT '关联救助记录ID',
  `adoption_id` BIGINT COMMENT '关联领养记录ID',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-否 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_report_user` (`report_user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_location` (`location_city`, `location_district`),
  INDEX `idx_animal_type` (`animal_type`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流浪动物表';

-- ==========================================
-- 救助相关表
-- ==========================================

-- 救助任务表
DROP TABLE IF EXISTS `rescue_task`;
CREATE TABLE `rescue_task` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
  `animal_id` BIGINT NOT NULL COMMENT '动物ID',
  `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
  `rescuer_id` BIGINT COMMENT '救助人ID',
  `title` VARCHAR(100) COMMENT '任务标题',
  `description` TEXT COMMENT '任务描述',
  `urgency` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '紧急程度 LOW-低 NORMAL-普通 HIGH-高 URGENT-紧急',
  `status` VARCHAR(20) DEFAULT 'PUBLISHED' COMMENT '状态 PUBLISHED-已发布 ACCEPTED-已接单 COMPLETED-已完成 CANCELLED-已取消',
  `accept_time` DATETIME COMMENT '接单时间',
  `complete_time` DATETIME COMMENT '完成时间',
  `rescue_images` TEXT COMMENT '救助过程图片，JSON数组',
  `rescue_note` TEXT COMMENT '救助备注',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_animal` (`animal_id`),
  INDEX `idx_publisher` (`publisher_id`),
  INDEX `idx_rescuer` (`rescuer_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_urgency` (`urgency`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='救助任务表';

-- ==========================================
-- 领养相关表
-- ==========================================

-- 领养申请表
DROP TABLE IF EXISTS `adoption_application`;
CREATE TABLE `adoption_application` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
  `animal_id` BIGINT NOT NULL COMMENT '动物ID',
  `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
  `applicant_name` VARCHAR(20) COMMENT '申请人姓名',
  `applicant_phone` VARCHAR(20) COMMENT '联系电话',
  `applicant_address` VARCHAR(200) COMMENT '居住地址',
  `housing_type` VARCHAR(20) COMMENT '住房类型 RENT-租房 OWN-自有',
  `has_experience` TINYINT DEFAULT 0 COMMENT '是否有养宠经验 0-否 1-是',
  `experience_desc` TEXT COMMENT '养宠经验描述',
  `family_agree` TINYINT DEFAULT 0 COMMENT '家人是否同意 0-否 1-是',
  `reason` TEXT COMMENT '领养原因',
  `commitment` TEXT COMMENT '领养承诺',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态 PENDING-待审核 APPROVED-已通过 REJECTED-已拒绝 COMPLETED-已完成',
  `reviewer_id` BIGINT COMMENT '审核人ID',
  `review_note` TEXT COMMENT '审核备注',
  `review_time` DATETIME COMMENT '审核时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_animal` (`animal_id`),
  INDEX `idx_applicant` (`applicant_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领养申请表';

-- 领养回访记录表
DROP TABLE IF EXISTS `adoption_followup`;
CREATE TABLE `adoption_followup` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回访ID',
  `adoption_id` BIGINT NOT NULL COMMENT '领养记录ID',
  `visitor_id` BIGINT NOT NULL COMMENT '回访人ID',
  `followup_date` DATE COMMENT '回访日期',
  `animal_status` VARCHAR(20) COMMENT '动物状况 GOOD-良好 NORMAL-一般 BAD-不佳',
  `images` TEXT COMMENT '回访图片，JSON数组',
  `note` TEXT COMMENT '回访记录',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_adoption` (`adoption_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领养回访记录表';

-- ==========================================
-- 捐赠相关表
-- ==========================================

-- 捐赠记录表
DROP TABLE IF EXISTS `donation_record`;
CREATE TABLE `donation_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '捐赠ID',
  `donor_id` BIGINT NOT NULL COMMENT '捐赠人ID',
  `donation_type` VARCHAR(20) COMMENT '捐赠类型 MONEY-资金 MATERIAL-物资',
  `amount` DECIMAL(10,2) COMMENT '捐赠金额（资金捐赠）',
  `material_name` VARCHAR(100) COMMENT '物资名称（物资捐赠）',
  `material_quantity` INT COMMENT '物资数量',
  `material_unit` VARCHAR(20) COMMENT '物资单位（如：袋、箱、个）',
  `target_id` BIGINT COMMENT '捐赠目标ID（可选，指定救助任务或动物）',
  `target_type` VARCHAR(20) COMMENT '目标类型 ANIMAL-动物 TASK-任务 GENERAL-通用',
  `order_no` VARCHAR(50) UNIQUE COMMENT '订单号',
  `payment_method` VARCHAR(20) COMMENT '支付方式 WECHAT-微信 ALIPAY-支付宝',
  `payment_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '支付状态 PENDING-待支付 SUCCESS-已支付 FAILED-失败 REFUND-已退款',
  `payment_time` DATETIME COMMENT '支付时间',
  `transaction_id` VARCHAR(100) COMMENT '第三方交易号',
  `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名 0-否 1-是',
  `message` VARCHAR(200) COMMENT '留言',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_donor` (`donor_id`),
  INDEX `idx_order` (`order_no`),
  INDEX `idx_status` (`payment_status`),
  INDEX `idx_type` (`donation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='捐赠记录表';

-- ==========================================
-- 社区相关表
-- ==========================================

-- 帖子表
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '发布人ID',
  `category` VARCHAR(20) COMMENT '分类 KNOWLEDGE-知识分享 DAILY-日常分享 HELP-求助 STORY-故事',
  `title` VARCHAR(100) COMMENT '标题',
  `content` TEXT COMMENT '内容',
  `images` TEXT COMMENT '图片列表，JSON数组',
  `video_url` VARCHAR(255) COMMENT '视频URL',
  `animal_id` BIGINT COMMENT '关联动物ID（可选）',
  `view_count` INT DEFAULT 0 COMMENT '浏览量',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT DEFAULT 0 COMMENT '评论数',
  `share_count` INT DEFAULT 0 COMMENT '分享数',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶 0-否 1-是',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-已删除 1-正常 2-待审核',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user` (`user_id`),
  INDEX `idx_category` (`category`),
  INDEX `idx_status` (`status`),
  INDEX `idx_create_time` (`create_time`),
  INDEX `idx_is_top` (`is_top`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';

-- 评论表
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '评论人ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID，0表示一级评论',
  `reply_to_id` BIGINT COMMENT '回复的用户ID',
  `content` VARCHAR(500) COMMENT '评论内容',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-已删除 1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_post` (`post_id`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 点赞记录表
DROP TABLE IF EXISTS `like_record`;
CREATE TABLE `like_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `target_type` VARCHAR(20) COMMENT '目标类型 POST-帖子 COMMENT-评论',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
  INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';

-- ==========================================
-- 系统相关表
-- ==========================================

-- 通知消息表
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
  `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
  `type` VARCHAR(20) COMMENT '通知类型 SYSTEM-系统 RESCUE-救助 ADOPTION-领养 COMMENT-评论 LIKE-点赞',
  `title` VARCHAR(100) COMMENT '标题',
  `content` VARCHAR(500) COMMENT '内容',
  `related_id` BIGINT COMMENT '关联ID',
  `related_type` VARCHAR(20) COMMENT '关联类型',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读 0-否 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_user` (`user_id`),
  INDEX `idx_is_read` (`is_read`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知消息表';

-- 系统配置表
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(50) UNIQUE COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `description` VARCHAR(200) COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 轮播图表
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '轮播图ID',
  `title` VARCHAR(100) COMMENT '标题',
  `image_url` VARCHAR(255) COMMENT '图片URL',
  `link_url` VARCHAR(255) COMMENT '跳转链接',
  `link_type` VARCHAR(20) COMMENT '链接类型 PAGE-页面 URL-外链',
  `sort_order` INT DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_status` (`status`),
  INDEX `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT COMMENT '操作用户ID',
  `username` VARCHAR(50) COMMENT '操作用户名',
  `operation` VARCHAR(50) COMMENT '操作类型',
  `method` VARCHAR(200) COMMENT '请求方法',
  `params` TEXT COMMENT '请求参数',
  `ip` VARCHAR(50) COMMENT 'IP地址',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_user` (`user_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ==========================================
-- 初始化数据
-- ==========================================

-- 管理员表
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
  `username` VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
  `real_name` VARCHAR(20) COMMENT '真实姓名',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(50) COMMENT '邮箱',
  `avatar` VARCHAR(255) COMMENT '头像',
  `role` VARCHAR(20) DEFAULT 'ADMIN' COMMENT '角色 SUPER_ADMIN-超级管理员 ADMIN-管理员',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_username` (`username`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 插入管理员账号（密码：admin123，MD5加密后）
INSERT INTO `admin_user` (`id`, `username`, `password`, `real_name`, `role`, `status`) 
VALUES 
(1, 'admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'SUPER_ADMIN', 1),
(2, 'manager', '1d0258c2440a8d19e716292b231e3190', '运营管理员', 'ADMIN', 1);

-- 插入测试用户（密码：user123，MD5加密后）
INSERT INTO `user` (`id`, `phone`, `nickname`, `avatar`, `gender`, `real_name`, `role`, `points`, `status`) 
VALUES 
(1, '13800138000', '爱心志愿者', 'https://img.yzcdn.cn/vant/cat.jpeg', 1, '张三', 'VOLUNTEER', 520, 1),
(2, '13800138001', '萌宠达人', 'https://img.yzcdn.cn/vant/cat.jpeg', 2, '李四', 'USER', 380, 1),
(3, '13800138002', '流浪猫救助者', 'https://img.yzcdn.cn/vant/cat.jpeg', 2, '王芳', 'VOLUNTEER', 650, 1),
(4, '13800138003', '小动物守护者', 'https://img.yzcdn.cn/vant/cat.jpeg', 1, '刘明', 'USER', 280, 1),
(5, '13800138004', '公益爱心人士', 'https://img.yzcdn.cn/vant/cat.jpeg', 2, '陈丽', 'USER', 450, 1);

-- 插入系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('site_name', '萌友益站', '网站名称'),
('site_logo', '', '网站Logo'),
('contact_phone', '', '联系电话'),
('contact_email', '', '联系邮箱'),
('points_sign', '5', '签到积分'),
('points_post', '10', '发帖积分'),
('points_rescue', '50', '救助积分'),
('points_adoption', '100', '领养积分'),
('points_donation', '20', '捐赠积分'),
('storage_type', 'local', '存储类型：local-本地存储，oss-阿里云OSS'),
('oss_access_key_id', '', '阿里云OSS AccessKeyId'),
('oss_access_key_secret', '', '阿里云OSS AccessKeySecret'),
('oss_endpoint', '', '阿里云OSS Endpoint'),
('oss_bucket_name', '', '阿里云OSS Bucket名称'),
('oss_domain', '', '阿里云OSS访问域名');

-- 插入轮播图
INSERT INTO `banner` (`id`, `title`, `image_url`, `link_url`, `link_type`, `sort_order`, `status`) VALUES
(1, '欢迎来到萌友益站', 'https://picsum.photos/750/400?random=1', '', 'PAGE', 1, 1),
(2, '一起救助流浪动物', 'https://picsum.photos/750/400?random=2', '', 'PAGE', 2, 1),
(3, '给它们一个温暖的家', 'https://picsum.photos/750/400?random=3', '', 'PAGE', 3, 1);

-- 插入流浪动物数据
INSERT INTO `stray_animal` (`id`, `report_user_id`, `animal_type`, `breed`, `gender`, `age_estimate`, `color`, `size`, `health_status`, `description`, `images`, `location_province`, `location_city`, `location_district`, `location_address`, `status`, `create_time`) VALUES
(1, 1, 'CAT', '中华田园猫', 2, '3个月', '橘色', 'SMALL', 'GOOD', '在小区花园发现的小橘猫，很亲人，应该是被遗弃的。', '["https://picsum.photos/400/300?random=10", "https://picsum.photos/400/300?random=11"]', '广东省', '深圳市', '南山区', '科技园地铁站附近', 'REPORTED', '2025-11-15 09:30:00'),
(2, 3, 'DOG', '金毛', 1, '1岁', '金黄色', 'LARGE', 'INJURED', '在马路边发现，右前腿受伤，性格温顺，急需救助。', '["https://picsum.photos/400/300?random=12", "https://picsum.photos/400/300?random=13"]', '广东省', '深圳市', '福田区', '华强北商业区', 'RESCUING', '2025-11-16 14:20:00'),
(3, 1, 'CAT', '美短', 2, '6个月', '银灰色虎斑', 'MEDIUM', 'GOOD', '小区楼道发现的美短猫咪，已绝育，非常健康。', '["https://picsum.photos/400/300?random=14"]', '广东省', '深圳市', '南山区', '蛇口海上世界', 'RESCUED', '2025-11-10 16:45:00'),
(4, 2, 'CAT', '中华田园猫', 1, '2个月', '白色带黑斑', 'SMALL', 'SICK', '在垃圾桶旁发现，有些感冒症状，需要治疗。', '["https://picsum.photos/400/300?random=15", "https://picsum.photos/400/300?random=16"]', '广东省', '深圳市', '宝安区', '西乡街道', 'RESCUING', '2025-11-18 10:15:00'),
(5, 3, 'DOG', '泰迪', 2, '2岁', '棕色', 'SMALL', 'GOOD', '公园发现的泰迪犬，很乖巧，寻找领养家庭。', '["https://picsum.photos/400/300?random=17"]', '广东省', '深圳市', '龙岗区', '龙城广场', 'RESCUED', '2025-11-12 11:30:00'),
(6, 1, 'CAT', '狸花猫', 1, '4个月', '灰色条纹', 'MEDIUM', 'GOOD', '地铁站发现的小猫，活泼可爱，已驱虫疫苗。', '["https://picsum.photos/400/300?random=18", "https://picsum.photos/400/300?random=19"]', '广东省', '深圳市', '南山区', '深圳湾公园', 'ADOPTED', '2025-11-05 08:20:00');

-- 插入救助任务
INSERT INTO `rescue_task` (`id`, `animal_id`, `publisher_id`, `rescuer_id`, `title`, `description`, `urgency`, `status`, `accept_time`, `complete_time`, `rescue_images`, `rescue_note`, `create_time`) VALUES
(1, 2, 3, 1, '紧急救助受伤金毛', '华强北商业区发现受伤金毛，右前腿骨折，需要紧急救助并送医。', 'URGENT', 'COMPLETED', '2025-11-16 15:00:00', '2025-11-16 18:30:00', '["https://picsum.photos/400/300?random=20", "https://picsum.photos/400/300?random=21"]', '已送往宠物医院，医生说需要手术，预计恢复期2周。', '2025-11-16 14:30:00'),
(2, 4, 2, 1, '救助生病小猫', '西乡街道垃圾桶旁发现的小猫咪，有感冒症状，需要送医检查。', 'HIGH', 'COMPLETED', '2025-11-18 11:00:00', '2025-11-18 14:20:00', '["https://picsum.photos/400/300?random=22"]', '已完成检查和治疗，现在恢复良好。', '2025-11-18 10:30:00'),
(3, 1, 1, NULL, '救助小区橘猫', '科技园地铁站附近发现的小橘猫，需要志愿者帮助救助。', 'NORMAL', 'PUBLISHED', NULL, NULL, NULL, NULL, '2025-11-15 10:00:00');

-- 插入领养申请
INSERT INTO `adoption_application` (`id`, `animal_id`, `applicant_id`, `applicant_name`, `applicant_phone`, `applicant_address`, `housing_type`, `has_experience`, `experience_desc`, `family_agree`, `reason`, `commitment`, `status`, `reviewer_id`, `review_note`, `review_time`, `create_time`) VALUES
(1, 6, 4, '刘明', '13800138003', '深圳市南山区科技园', 'OWN', 1, '之前养过一只英短，有3年养猫经验，熟悉猫咪的生活习性和日常照料。', 1, '家里之前的猫咪因病去世了，家人都很想念它。希望能给这只小猫一个温暖的家。', '承诺会好好照顾它，按时接种疫苗，生病及时就医，绝不抛弃。', 'COMPLETED', 1, '申请人条件符合，家庭环境良好，已完成领养。', '2025-11-06 10:00:00', '2025-11-05 15:00:00'),
(2, 5, 5, '陈丽', '13800138004', '深圳市福田区莲花北', 'RENT', 0, '虽然没有养宠经验，但很喜欢小动物，已经做好了充分的准备。', 1, '一直想养一只狗狗陪伴，看到这只泰迪很喜欢，希望能给它一个家。', '承诺每天遛狗，按时喂食，定期体检，不离不弃。', 'APPROVED', 1, '申请通过，等待完成领养手续。', '2025-11-13 16:30:00', '2025-11-12 14:00:00'),
(3, 3, 2, '李四', '13800138001', '深圳市南山区蛇口', 'OWN', 1, '家里已经有一只猫了，相处融洽，有丰富的养猫经验。', 1, '想给家里的猫咪找个伴，看到这只美短很合适。', '承诺科学喂养，提供良好的生活环境。', 'PENDING', NULL, NULL, NULL, '2025-11-11 09:00:00');

-- 插入捐赠记录
INSERT INTO `donation_record` (`id`, `donor_id`, `donation_type`, `amount`, `material_name`, `material_quantity`, `material_unit`, `target_type`, `order_no`, `payment_method`, `payment_status`, `payment_time`, `is_anonymous`, `message`, `create_time`) VALUES
(1, 2, 'MONEY', 100.00, NULL, NULL, NULL, 'GENERAL', 'D202511150001', 'WECHAT', 'SUCCESS', '2025-11-15 10:30:00', 0, '希望能帮助更多流浪动物', '2025-11-15 10:30:00'),
(2, 4, 'MONEY', 200.00, NULL, NULL, NULL, 'GENERAL', 'D202511160001', 'WECHAT', 'SUCCESS', '2025-11-16 14:20:00', 0, '支持公益事业', '2025-11-16 14:20:00'),
(3, 5, 'MATERIAL', NULL, '猫粮', 10, '袋', 'GENERAL', 'D202511170001', 'WECHAT', 'SUCCESS', '2025-11-17 09:15:00', 0, '捐赠一些猫粮', '2025-11-17 09:15:00'),
(4, 1, 'MONEY', 500.00, NULL, NULL, NULL, 'ANIMAL', 'D202511180001', 'WECHAT', 'SUCCESS', '2025-11-18 16:45:00', 0, '专项用于受伤金毛的医疗费用', '2025-11-18 16:45:00'),
(5, 3, 'MATERIAL', NULL, '狗粮', 5, '袋', 'GENERAL', 'D202511190001', 'WECHAT', 'SUCCESS', '2025-11-19 11:20:00', 0, '捐赠狗粮', '2025-11-19 11:20:00');

-- 插入社区帖子
INSERT INTO `post` (`id`, `user_id`, `category`, `title`, `content`, `images`, `video_url`, `view_count`, `like_count`, `comment_count`, `status`, `create_time`) VALUES
(1, 1, 'STORY', '救助小橘猫的故事', '今天在小区发现了一只可爱的小橘猫，看起来是被遗弃的。它很亲人，一直跟着我喵喵叫。已经带回家洗澡了，准备找个靠谱的领养家庭。', '["https://picsum.photos/400/300?random=30", "https://picsum.photos/400/300?random=31"]', NULL, 156, 28, 12, 1, '2025-11-15 20:30:00'),
(2, 3, 'KNOWLEDGE', '如何正确喂养流浪猫', '很多朋友想帮助流浪猫，但不知道如何正确喂养。这里分享一些经验：\n1. 选择优质猫粮，避免喂食人类食物\n2. 提供干净的饮用水\n3. 定期驱虫和疫苗\n4. 观察健康状况，及时就医', '["https://picsum.photos/400/300?random=32"]', NULL, 234, 45, 18, 1, '2025-11-14 15:20:00'),
(3, 2, 'DAILY', '今天带毛孩子去公园玩啦', '天气真好，带着我家泰迪去公园玩，它玩得超级开心！遇到了好多同样喜欢狗狗的朋友。', '["https://picsum.photos/400/300?random=33", "https://picsum.photos/400/300?random=34", "https://picsum.photos/400/300?random=35"]', NULL, 189, 34, 8, 1, '2025-11-13 17:45:00'),
(4, 3, 'HELP', '求助！狗狗突然不吃东西了', '我家金毛这两天突然不爱吃东西了，精神也不太好，有没有懂的朋友帮忙看看是什么情况？', '["https://picsum.photos/400/300?random=36"]', NULL, 267, 15, 25, 1, '2025-11-16 10:15:00'),
(5, 1, 'STORY', '又一次成功救助', '今天又成功救助了一只受伤的流浪猫，虽然过程很辛苦，但看到它康复的样子，一切都值得了！', '["https://picsum.photos/400/300?random=37", "https://picsum.photos/400/300?random=38"]', NULL, 312, 56, 21, 1, '2025-11-17 19:30:00');

-- 插入评论
INSERT INTO `comment` (`id`, `post_id`, `user_id`, `parent_id`, `reply_to_id`, `content`, `like_count`, `status`, `create_time`) VALUES
(1, 1, 2, 0, NULL, '小橘猫好可爱啊！希望能找到好人家', 5, 1, '2025-11-15 21:00:00'),
(2, 1, 4, 0, NULL, '我想领养！可以联系我吗？', 3, 1, '2025-11-15 21:30:00'),
(3, 1, 1, 0, 2, '好的，我私信你哈', 2, 1, '2025-11-15 22:00:00'),
(4, 2, 5, 0, NULL, '学到了很多，感谢分享！', 8, 1, '2025-11-14 16:00:00'),
(5, 3, 4, 0, NULL, '好可爱的狗狗！', 4, 1, '2025-11-13 18:00:00'),
(6, 4, 1, 0, NULL, '建议尽快去宠物医院检查一下', 12, 1, '2025-11-16 10:30:00'),
(7, 5, 2, 0, NULL, '太棒了！向你学习！', 7, 1, '2025-11-17 20:00:00');

-- 插入积分记录
INSERT INTO `user_points_record` (`id`, `user_id`, `points`, `type`, `description`, `create_time`) VALUES
(1, 1, 5, 'SIGN', '每日签到', '2025-11-15 08:00:00'),
(2, 1, 10, 'POST', '发布帖子', '2025-11-15 20:30:00'),
(3, 1, 50, 'RESCUE', '完成救助任务', '2025-11-16 18:30:00'),
(4, 2, 5, 'SIGN', '每日签到', '2025-11-16 09:00:00'),
(5, 2, 10, 'POST', '发布帖子', '2025-11-13 17:45:00'),
(6, 3, 50, 'RESCUE', '完成救助任务', '2025-11-18 14:20:00'),
(7, 4, 100, 'ADOPTION', '完成领养', '2025-11-06 10:00:00'),
(8, 5, 20, 'DONATION', '爱心捐赠', '2025-11-17 09:15:00');

-- 插入通知消息
INSERT INTO `notification` (`id`, `user_id`, `type`, `title`, `content`, `related_id`, `related_type`, `is_read`, `create_time`) VALUES
(1, 4, 'ADOPTION', '领养申请通过', '您的领养申请已通过审核，请尽快完成领养手续。', 1, 'ADOPTION', 1, '2025-11-06 10:00:00'),
(2, 5, 'ADOPTION', '领养申请通过', '您的领养申请已通过审核，请尽快完成领养手续。', 2, 'ADOPTION', 0, '2025-11-13 16:30:00'),
(3, 1, 'RESCUE', '救助任务已接单', '您发布的救助任务已被志愿者接单。', 1, 'RESCUE', 1, '2025-11-16 15:00:00'),
(4, 3, 'COMMENT', '新评论通知', '您的帖子收到了新评论。', 2, 'POST', 1, '2025-11-14 16:00:00'),
(5, 1, 'LIKE', '点赞通知', '您的帖子收到了点赞。', 1, 'POST', 0, '2025-11-15 21:30:00');

-- ==========================================
-- 数据库初始化完成
-- ==========================================
