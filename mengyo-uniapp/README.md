# 萌友益站 - UniApp小程序

## 项目介绍

萌友益站是一个公益救助小程序，专注于流浪动物救助、领养、捐赠等功能。

## 技术栈

- UniApp 3.x
- Vue 3
- Sass
- 微信小程序

## 项目结构

```
mengyo-uniapp/
├── pages/              # 页面文件
│   ├── index/         # 首页
│   ├── login/         # 登录页
│   ├── user/          # 用户中心
│   ├── rescue/        # 救助任务
│   ├── animal/        # 流浪动物
│   └── community/     # 社区
├── utils/             # 工具类
│   ├── request.js     # 网络请求封装
│   └── api.js         # API接口定义
├── static/            # 静态资源
├── App.vue            # 应用入口
├── main.js            # 主入口文件
├── pages.json         # 页面配置
├── manifest.json      # 应用配置
└── package.json       # 依赖配置
```

## 开发指南

### 安装依赖

```bash
npm install
```

### 运行开发

微信小程序：
```bash
npm run dev:mp-weixin
```

H5：
```bash
npm run dev:h5
```

### 构建发布

微信小程序：
```bash
npm run build:mp-weixin
```

## 功能模块

- [x] 用户登录（微信登录、手机号登录）
- [x] 用户中心
- [ ] 救助任务
- [ ] 流浪动物
- [ ] 领养申请
- [ ] 爱心捐赠
- [ ] 社区动态
- [ ] 积分系统

## 注意事项

1. 需要在 `utils/request.js` 中配置正确的后端API地址
2. 需要在 `manifest.json` 中配置微信小程序的AppID
3. 图标资源需要放置在 `static` 目录下

## 联系方式

如有问题，请联系开发团队。
