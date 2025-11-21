# 萌友益站 - 公益救助平台

<div align="center">

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-brightgreen.svg)](https://vuejs.org/)
[![UniApp](https://img.shields.io/badge/UniApp-latest-brightgreen.svg)](https://uniapp.dcloud.net.cn/)

</div>

## 📖 项目简介

萌友益站是一个专为喜爱小动物、关爱流浪动物的人士打造的公益救助平台。在这里，你可以与众多萌友一起，为流浪动物提供救助、领养和捐赠等帮助，同时还能与其他养宠人交流互动，分享养宠经验和乐趣。

我们致力于打造一个充满爱心、温暖和趣味的萌宠社区，让每一只流浪动物都能找到属于自己的家，让每一位萌友都能在这里收获快乐和感动。

## ✨ 核心功能

### 🐾 流浪动物救助
- 流浪动物报备（位置、照片、描述）
- 救助任务发布与接单
- 救助进度实时跟踪
- 救助历史记录查询

### 💝 领养中心
- 待领养动物详细展示
- 在线提交领养申请
- 领养审核流程管理
- 领养后定期回访

### 🎁 爱心捐赠
- 资金捐赠（微信支付）
- 物资捐赠登记
- 捐赠记录透明公开
- 爱心榜单展示

### 🌟 社区交流
- 发布养宠动态（图文、视频）
- 知识经验分享
- 评论互动点赞
- 用户间私信交流

### 📊 数据统计
- 救助数据分析
- 领养数据统计
- 捐赠数据汇总
- 用户活跃度分析

## 🛠️ 技术架构

### 后端技术栈
- **核心框架**: Spring Boot 3.2.0
- **持久层**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **API文档**: Knife4j (Swagger 3)
- **认证**: JWT
- **工具库**: Hutool

### 小程序端
- **框架**: UniApp
- **UI组件**: uni-ui
- **状态管理**: Vuex
- **支持平台**: 微信小程序、H5、APP

### 管理后台
- **框架**: Vue 3 + Vite
- **UI组件**: Element Plus
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **图表**: ECharts

## 📁 项目结构

```
mengyo/
├── mengyo-backend/           # 后端服务
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   ├── pom.xml
│   └── README.md
├── mengyo-miniapp/           # 小程序端（待创建）
│   ├── pages/
│   ├── components/
│   ├── utils/
│   └── README.md
├── mengyo-admin/             # 管理后台（待创建）
│   ├── src/
│   ├── package.json
│   └── README.md
├── database/                 # 数据库脚本
│   └── init.sql
├── docs/                     # 项目文档
│   ├── 项目架构设计文档.md
│   ├── 开发进度跟踪.md
│   └── 第一阶段-基础框架开发计划.md
└── README.md                 # 项目说明
```

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- Node.js 16+

### 1. 克隆项目

```bash
git clone https://github.com/mengyo/mengyo-platform.git
cd mengyo-platform
```

### 2. 初始化数据库

```bash
mysql -u root -p < database/init.sql
```

### 3. 配置后端

修改 `mengyo-backend/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mengyo_db
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      password: your_password
```

### 4. 启动后端服务

```bash
cd mengyo-backend
mvn spring-boot:run
```

访问 API文档：http://localhost:8080/api/doc.html

### 5. 启动小程序（开发中）

```bash
cd mengyo-miniapp
npm install
npm run dev:mp-weixin
```

### 6. 启动管理后台（开发中）

```bash
cd mengyo-admin
npm install
npm run dev
```

## 📋 开发进度

### 第一阶段：基础框架搭建 [进行中]

#### 已完成 ✅
- [x] 项目架构设计文档
- [x] 数据库表设计和初始化脚本
- [x] Spring Boot 3 项目创建
- [x] MyBatis-Plus集成配置
- [x] Redis缓存配置
- [x] Swagger API文档配置
- [x] JWT认证工具实现
- [x] 统一响应格式
- [x] 全局异常处理
- [x] 基础工具类封装

#### 进行中 🔄
- [ ] 用户注册/登录接口
- [ ] 用户认证拦截器
- [ ] 文件上传功能
- [ ] 小程序端项目创建
- [ ] 管理后台项目创建

### 第二阶段：用户系统 [未开始]
- 微信授权登录
- 手机号注册/登录
- 用户信息管理
- 实名认证
- 积分系统

### 后续阶段
详见 [开发进度跟踪文档](docs/开发进度跟踪.md)

## 📖 文档

- [项目架构设计文档](项目架构设计文档.md)
- [开发进度跟踪](开发进度跟踪.md)
- [第一阶段开发计划](第一阶段-基础框架开发计划.md)
- [后端开发文档](mengyo-backend/README.md)

## 🤝 参与贡献

我们欢迎所有形式的贡献，无论是新功能、Bug修复还是文档改进。

### 贡献步骤

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m '[feat] Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

### 开发规范

- 遵循阿里巴巴Java开发规范
- 代码需要添加详细注释
- 提交信息使用规范格式
- 编写单元测试

## 📄 开源协议

本项目采用 [Apache 2.0](LICENSE) 开源协议

## 📮 联系我们

- **项目主页**: https://mengyo.com
- **问题反馈**: https://github.com/mengyo/mengyo-platform/issues
- **邮箱**: support@mengyo.com
- **微信公众号**: 萌友益站

## 💖 致谢

感谢所有为流浪动物救助事业贡献力量的志愿者们！

感谢以下开源项目：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Vue.js](https://vuejs.org/)
- [UniApp](https://uniapp.dcloud.net.cn/)
- [Element Plus](https://element-plus.org/)

---

<div align="center">

**用爱心温暖每一只流浪的生命** 🐾

Made with ❤️ by Mengyo Team

</div>
