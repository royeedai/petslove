# 萌友益站 - 管理后台

## 项目介绍

萌友益站管理后台是一个基于 Vue 3 + Vite + Element Plus 开发的后台管理系统，用于管理流浪动物救助、领养、捐赠等相关业务。

## 技术栈

- **前端框架**: Vue 3.3+
- **构建工具**: Vite 5.0+
- **UI框架**: Element Plus 2.4+
- **状态管理**: Pinia 2.1+
- **路由管理**: Vue Router 4.2+
- **HTTP请求**: Axios 1.6+
- **CSS预处理**: Sass

## 项目结构

```
mengyo-admin/
├── public/              # 静态资源
├── src/
│   ├── api/            # API接口
│   ├── assets/         # 资源文件
│   ├── components/     # 公共组件
│   ├── layout/         # 布局组件
│   ├── router/         # 路由配置
│   ├── stores/         # 状态管理
│   ├── styles/         # 全局样式
│   ├── utils/          # 工具函数
│   ├── views/          # 页面视图
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── index.html          # HTML模板
├── vite.config.js      # Vite配置
└── package.json        # 依赖配置
```

## 功能模块

### 已完成
- [x] 用户登录（手机号+验证码）
- [x] 基础布局（侧边栏、顶部导航、内容区）
- [x] 路由配置与权限控制
- [x] 用户管理（用户列表、搜索、状态管理）
- [x] 数据概览（Dashboard）

### 待开发
- [ ] 积分管理
- [ ] 动物管理
- [ ] 救助任务管理
- [ ] 领养申请管理
- [ ] 捐赠记录管理
- [ ] 社区管理（帖子、评论）
- [ ] 系统配置
- [ ] 轮播图管理

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

访问地址：http://localhost:3000

### 生产构建

```bash
npm run build
```

### 预览构建

```bash
npm run preview
```

## 开发规范

### 目录规范

- 页面文件放在 `src/views` 目录下，按模块分类
- 公共组件放在 `src/components` 目录下
- API接口放在 `src/api` 目录下，按模块分类
- 工具函数放在 `src/utils` 目录下

### 命名规范

- 组件名使用 PascalCase（大驼峰）
- 文件名使用 kebab-case（短横线）
- 变量名使用 camelCase（小驼峰）
- 常量名使用 UPPER_CASE（大写+下划线）

### 代码规范

- 使用 Vue 3 Composition API（setup语法糖）
- 使用 ES6+ 语法
- 统一使用单引号
- 合理使用注释

## 接口说明

后端API地址配置在 `src/utils/request.js` 中：

```javascript
baseURL: '/api'  // 开发环境会代理到 http://localhost:8080
```

### 主要接口

- 登录: `POST /api/user/phone-login`
- 获取用户信息: `GET /api/user/info`
- 用户列表: `GET /api/user/list`
- 积分记录: `GET /api/user/points/records`

## 注意事项

1. 确保后端服务已启动（默认端口8080）
2. 开发环境使用代理转发API请求
3. 生产环境需要配置正确的API地址
4. 图标资源需要放置在相应目录

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 13
- Edge >= 88

## License

MIT License

## 联系方式

如有问题，请联系开发团队。
