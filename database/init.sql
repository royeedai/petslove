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

-- 插入管理员账号
INSERT INTO `user` (`id`, `phone`, `nickname`, `avatar`, `role`, `status`) 
VALUES (1, 'admin', '系统管理员', '', 'ADMIN', 1);

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
('points_donation', '20', '捐赠积分');

-- ==========================================
-- 数据库初始化完成
-- ==========================================
