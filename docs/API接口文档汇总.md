# 萌友益站 - API接口文档汇总

**版本**: v1.0  
**Base URL**: `http://localhost:8080/api`  
**更新日期**: 2025-11-21

---

## 📖 接口说明

### 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1700000000000
}
```

### 状态码说明
- `200`: 成功
- `400`: 请求参数错误
- `401`: 未授权
- `403`: 无权限
- `404`: 资源不存在
- `500`: 服务器错误

### 请求头说明
```
Content-Type: application/json
userId: {用户ID} // 需要认证的接口必须传递
```

---

## 1️⃣ 用户模块 (User Module)

**Base Path**: `/api/user`

### 1.1 微信登录
```
POST /wx-login
```

**请求参数**:
```json
{
  "code": "微信授权码"
}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "token": "JWT令牌",
    "userInfo": {
      "id": 1,
      "nickname": "用户昵称",
      "avatar": "头像URL"
    }
  }
}
```

### 1.2 手机号登录
```
POST /phone-login
```

**请求参数**:
```json
{
  "phone": "13800138000",
  "code": "验证码"
}
```

### 1.3 发送验证码
```
POST /send-code?phone=13800138000
```

### 1.4 获取用户信息
```
GET /info
Header: userId={用户ID}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "nickname": "用户昵称",
    "avatar": "头像URL",
    "phone": "138****8000",
    "points": 100,
    "isVerified": 0
  }
}
```

### 1.5 更新用户信息
```
PUT /info
Header: userId={用户ID}
```

**请求参数**:
```json
{
  "nickname": "新昵称",
  "avatar": "新头像URL",
  "gender": 1
}
```

### 1.6 实名认证
```
POST /verify
Header: userId={用户ID}
```

**请求参数**:
```json
{
  "realName": "张三",
  "idCard": "身份证号"
}
```

### 1.7 积分记录
```
GET /points/records?page=1&size=10
Header: userId={用户ID}
```

### 1.8 增加积分
```
POST /points/add
Header: userId={用户ID}
```

**请求参数**:
```json
{
  "points": 10,
  "type": "SIGN",
  "description": "签到奖励"
}
```

---

## 2️⃣ 救助管理模块 (Rescue Module)

**Base Path**: `/api/rescue`

### 2.1 报备流浪动物
```
POST /animal/report
Header: userId={用户ID}
```

**请求参数**:
```json
{
  "animalType": "CAT",
  "breed": "中华田园猫",
  "gender": 1,
  "ageEstimate": "2个月",
  "color": "黄白色",
  "size": "SMALL",
  "healthStatus": "INJURED",
  "description": "在小区门口发现，后腿受伤",
  "images": ["图片URL1", "图片URL2"],
  "locationProvince": "广东省",
  "locationCity": "深圳市",
  "locationDistrict": "南山区",
  "locationAddress": "科技园地铁站A出口",
  "locationLat": 22.5428,
  "locationLng": 113.9434
}
```

**响应**:
```json
{
  "code": 200,
  "data": 1  // 动物ID
}
```

### 2.2 获取流浪动物列表
```
GET /animals?page=1&size=10&animalType=CAT&status=REPORTED&city=深圳市
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "animalType": "CAT",
        "breed": "中华田园猫",
        "healthStatus": "INJURED",
        "locationCity": "深圳市",
        "locationAddress": "科技园地铁站",
        "status": "REPORTED",
        "images": "[\"url1\", \"url2\"]",
        "createTime": "2025-11-21 10:00:00"
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10
  }
}
```

### 2.3 获取动物详情
```
GET /animal/{id}
```

### 2.4 发布救助任务
```
POST /task/publish
Header: userId={用户ID}
```

**请求参数**:
```json
{
  "animalId": 1,
  "title": "紧急救助受伤小猫",
  "description": "该猫咪后腿受伤，需要立即送医",
  "urgency": "URGENT"
}
```

### 2.5 获取救助任务列表
```
GET /tasks?page=1&size=10&status=PUBLISHED&urgency=URGENT
```

### 2.6 获取任务详情
```
GET /task/{id}
```

### 2.7 接受救助任务
```
POST /task/{id}/accept
Header: userId={用户ID}
```

### 2.8 完成救助任务
```
POST /task/{id}/complete?rescueNote=救助成功
Header: userId={用户ID}
```

**请求参数**:
```
rescueImages: ["图片URL1", "图片URL2"]
rescueNote: "救助备注"
```

### 2.9 取消救助任务
```
POST /task/{id}/cancel
Header: userId={用户ID}
```

---

## 3️⃣ 领养中心模块 (Adoption Module)

**Base Path**: `/api/adoption`

### 3.1 提交领养申请
```
POST /apply
Header: userId={用户ID}
```

**请求参数**:
```json
{
  "animalId": 1,
  "applicantName": "张三",
  "applicantPhone": "13800138000",
  "applicantAddress": "深圳市南山区科技园",
  "housingType": "OWN",
  "hasExperience": 1,
  "experienceDesc": "养过2年猫咪",
  "familyAgree": 1,
  "reason": "喜欢小动物，想给它一个温暖的家",
  "commitment": "我承诺会善待它，定期体检，不离不弃"
}
```

**响应**:
```json
{
  "code": 200,
  "data": 1  // 申请ID
}
```

### 3.2 获取领养申请列表
```
GET /applications?page=1&size=10&status=PENDING
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "animalId": 1,
        "applicantName": "张三",
        "applicantPhone": "138****8000",
        "status": "PENDING",
        "createTime": "2025-11-21 10:00:00"
      }
    ],
    "total": 50,
    "current": 1,
    "size": 10
  }
}
```

### 3.3 获取我的领养申请
```
GET /my-applications
Header: userId={用户ID}
```

### 3.4 获取申请详情
```
GET /application/{id}
```

### 3.5 审核领养申请
```
POST /application/{id}/review
Header: userId={管理员ID}
```

**请求参数**:
```json
{
  "result": "APPROVED",
  "reviewNote": "申请人条件良好，同意领养"
}
```

### 3.6 添加回访记录
```
POST /application/{id}/followup
Header: userId={回访人ID}
```

**请求参数**:
```json
{
  "followupDate": "2025-11-21",
  "animalStatus": "GOOD",
  "images": ["图片URL1", "图片URL2"],
  "note": "动物健康状况良好，适应新环境"
}
```

### 3.7 获取回访记录列表
```
GET /application/{id}/followups
```

---

## 4️⃣ 捐赠模块 (Donation Module)

**Base Path**: `/api/donation`

### 4.1 创建资金捐赠订单
```
POST /money
Header: userId={用户ID}
```

**请求参数**:
```json
{
  "amount": 100.00,
  "targetType": "GENERAL",
  "targetId": null,
  "paymentMethod": "WECHAT",
  "isAnonymous": 0,
  "message": "希望能帮助更多小动物"
}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "orderId": 1,
    "orderNo": "DN1732175123456789",
    "amount": 100.00,
    "paymentMethod": "WECHAT",
    "paymentParams": "支付参数"
  }
}
```

### 4.2 创建物资捐赠记录
```
POST /material
Header: userId={用户ID}
```

**请求参数**:
```json
{
  "materialName": "猫粮",
  "materialQuantity": 10,
  "materialUnit": "袋",
  "targetType": "GENERAL",
  "targetId": null,
  "isAnonymous": 0,
  "message": "希望小动物们健康成长"
}
```

### 4.3 获取捐赠记录列表
```
GET /list?page=1&size=10&donationType=MONEY
```

### 4.4 获取我的捐赠记录
```
GET /my-donations
Header: userId={用户ID}
```

### 4.5 获取捐赠榜单
```
GET /ranking?limit=10
```

### 4.6 支付回调接口
```
POST /callback?orderNo={订单号}&transactionId={交易号}&paymentStatus=SUCCESS
```

---

## 5️⃣ 社区交流模块 (Community Module)

**Base Path**: `/api/community`

### 5.1 发布帖子
```
POST /post
Header: userId={用户ID}
```

**请求参数**:
```json
{
  "category": "DAILY",
  "title": "我家猫咪的日常",
  "content": "今天猫咪特别乖...",
  "images": ["图片URL1", "图片URL2"],
  "videoUrl": "",
  "animalId": null
}
```

**响应**:
```json
{
  "code": 200,
  "data": 1  // 帖子ID
}
```

### 5.2 获取帖子列表
```
GET /posts?page=1&size=10&category=DAILY
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "category": "DAILY",
        "title": "我家猫咪的日常",
        "content": "今天猫咪特别乖...",
        "viewCount": 100,
        "likeCount": 20,
        "commentCount": 5,
        "isTop": 0,
        "createTime": "2025-11-21 10:00:00"
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10
  }
}
```

### 5.3 获取帖子详情
```
GET /post/{id}
```

### 5.4 删除帖子
```
DELETE /post/{id}
Header: userId={用户ID}
```

### 5.5 发表评论
```
POST /comment
Header: userId={用户ID}
```

**请求参数**:
```json
{
  "postId": 1,
  "parentId": 0,
  "replyToId": null,
  "content": "很有帮助，谢谢分享"
}
```

**响应**:
```json
{
  "code": 200,
  "data": 1  // 评论ID
}
```

### 5.6 获取评论列表
```
GET /post/{id}/comments
```

**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "postId": 1,
      "userId": 2,
      "parentId": 0,
      "content": "很有帮助，谢谢分享",
      "likeCount": 5,
      "createTime": "2025-11-21 10:05:00"
    }
  ]
}
```

### 5.7 删除评论
```
DELETE /comment/{id}
Header: userId={用户ID}
```

### 5.8 点赞/取消点赞
```
POST /like?targetType=POST&targetId=1
Header: userId={用户ID}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "liked": true  // true-已点赞，false-已取消
  }
}
```

### 5.9 检查是否已点赞
```
GET /is-liked?targetType=POST&targetId=1
Header: userId={用户ID}
```

**响应**:
```json
{
  "code": 200,
  "data": true
}
```

---

## 6️⃣ 通知系统模块 (Notification Module)

**Base Path**: `/api/notification`

### 6.1 获取通知列表
```
GET /list?page=1&size=10
Header: userId={用户ID}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "type": "RESCUE",
        "title": "救助任务提醒",
        "content": "您发布的救助任务已被接单",
        "relatedId": 1,
        "relatedType": "TASK",
        "isRead": 0,
        "createTime": "2025-11-21 10:00:00"
      }
    ],
    "total": 50,
    "current": 1,
    "size": 10
  }
}
```

### 6.2 获取未读通知数量
```
GET /unread-count
Header: userId={用户ID}
```

**响应**:
```json
{
  "code": 200,
  "data": 5
}
```

### 6.3 标记为已读
```
PUT /read?notificationId=1
Header: userId={用户ID}
```

**说明**: 不传`notificationId`则标记全部为已读

### 6.4 删除通知
```
DELETE /{id}
Header: userId={用户ID}
```

---

## 7️⃣ 系统管理模块 (System Module)

**Base Path**: `/api/system`

### 7.1 获取轮播图列表
```
GET /banners?status=1
```

**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "欢迎使用萌友益站",
      "imageUrl": "图片URL",
      "linkUrl": "/pages/index/index",
      "linkType": "PAGE",
      "sortOrder": 1,
      "status": 1
    }
  ]
}
```

### 7.2 保存或更新轮播图
```
POST /banner
```

**请求参数**:
```json
{
  "id": null,  // 更新时传ID
  "title": "标题",
  "imageUrl": "图片URL",
  "linkUrl": "跳转链接",
  "linkType": "PAGE",
  "sortOrder": 1,
  "status": 1
}
```

### 7.3 删除轮播图
```
DELETE /banner/{id}
```

### 7.4 获取系统配置
```
GET /config/{key}
```

**示例**:
```
GET /config/site_name
```

**响应**:
```json
{
  "code": 200,
  "data": "萌友益站"
}
```

### 7.5 获取所有配置
```
GET /configs
```

**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "configKey": "site_name",
      "configValue": "萌友益站",
      "description": "网站名称"
    }
  ]
}
```

### 7.6 保存或更新配置
```
POST /config?configKey=site_name&configValue=新名称&description=网站名称
```

---

## 📊 数据字典

### 动物类型 (animalType)
- `CAT`: 猫
- `DOG`: 狗
- `OTHER`: 其他

### 动物状态 (status)
- `REPORTED`: 已报备
- `RESCUING`: 救助中
- `RESCUED`: 已救助
- `ADOPTED`: 已领养
- `DEAD`: 已死亡

### 健康状况 (healthStatus)
- `GOOD`: 良好
- `INJURED`: 受伤
- `SICK`: 生病

### 体型 (size)
- `SMALL`: 小型
- `MEDIUM`: 中型
- `LARGE`: 大型

### 紧急程度 (urgency)
- `LOW`: 低
- `NORMAL`: 普通
- `HIGH`: 高
- `URGENT`: 紧急

### 救助任务状态 (status)
- `PUBLISHED`: 已发布
- `ACCEPTED`: 已接单
- `COMPLETED`: 已完成
- `CANCELLED`: 已取消

### 领养申请状态 (status)
- `PENDING`: 待审核
- `APPROVED`: 已通过
- `REJECTED`: 已拒绝
- `COMPLETED`: 已完成

### 住房类型 (housingType)
- `RENT`: 租房
- `OWN`: 自有

### 捐赠类型 (donationType)
- `MONEY`: 资金
- `MATERIAL`: 物资

### 支付方式 (paymentMethod)
- `WECHAT`: 微信
- `ALIPAY`: 支付宝

### 支付状态 (paymentStatus)
- `PENDING`: 待支付
- `SUCCESS`: 已支付
- `FAILED`: 失败
- `REFUND`: 已退款

### 目标类型 (targetType)
- `ANIMAL`: 动物
- `TASK`: 任务
- `GENERAL`: 通用

### 帖子分类 (category)
- `KNOWLEDGE`: 知识分享
- `DAILY`: 日常分享
- `HELP`: 求助
- `STORY`: 故事

### 通知类型 (type)
- `SYSTEM`: 系统通知
- `RESCUE`: 救助通知
- `ADOPTION`: 领养通知
- `COMMENT`: 评论通知
- `LIKE`: 点赞通知

### 点赞目标类型 (targetType)
- `POST`: 帖子
- `COMMENT`: 评论

### 轮播图链接类型 (linkType)
- `PAGE`: 页面
- `URL`: 外链

---

## 🔐 接口权限说明

### 公开接口（无需登录）
- 获取轮播图列表
- 获取系统配置
- 获取流浪动物列表
- 获取救助任务列表
- 获取帖子列表
- 获取捐赠记录列表

### 普通用户权限
- 所有公开接口
- 用户相关接口（查看/修改个人信息）
- 报备流浪动物
- 发布/接受/完成救助任务
- 提交领养申请
- 创建捐赠
- 发布帖子/评论/点赞

### 管理员权限
- 所有普通用户权限
- 审核领养申请
- 添加回访记录
- 管理轮播图
- 管理系统配置
- 内容审核

---

## 📝 注意事项

1. 所有需要认证的接口必须在请求头中携带`userId`
2. 分页查询的`page`从1开始，不是0
3. 图片URL需要先通过文件上传接口获取（待实现）
4. 支付相关接口当前为模拟实现，需对接真实支付平台
5. 部分接口响应中的JSON字符串字段（如images）需要前端解析
6. 建议使用Swagger UI进行接口测试：`http://localhost:8080/api/doc.html`

---

## 🚀 快速开始

### 1. 启动项目
```bash
cd mengyo-backend
mvn spring-boot:run
```

### 2. 访问API文档
```
http://localhost:8080/api/doc.html
```

### 3. 测试接口
使用Swagger UI或Postman进行接口测试

---

**文档更新日期**: 2025-11-21  
**API版本**: v1.0  
**技术支持**: Mengyo Team
