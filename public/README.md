# Public 目录说明

这个目录用于存放三端编译后的文件，方便部署。

## 目录结构

```
public/
├── admin/          # 管理后台编译后的文件
├── uniapp/         # uni-app 编译后的文件（H5版本）
├── backend/        # 后端打包后的 JAR 文件
└── README.md       # 本说明文件
```

## 使用说明

### 1. 管理后台编译

```bash
cd mengyo-admin
npm run build
# 编译后的文件会生成在 dist 目录
# 将 dist 目录下的所有文件复制到 public/admin 目录
```

### 2. uni-app 编译（H5版本）

```bash
cd mengyo-uniapp
npm run build:h5
# 编译后的文件会生成在 unpackage/dist/build/h5 目录
# 将该目录下的所有文件复制到 public/uniapp 目录
```

### 3. 后端打包

```bash
cd mengyo-backend
mvn clean package -DskipTests
# 打包后的 JAR 文件会生成在 target 目录
# 将 target/mengyo-backend-1.0.0.jar 复制到 public/backend 目录
```

## 部署说明

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    # 管理后台
    location /admin {
        alias /path/to/public/admin;
        try_files $uri $uri/ /admin/index.html;
    }

    # uni-app H5版本
    location /app {
        alias /path/to/public/uniapp;
        try_files $uri $uri/ /app/index.html;
    }

    # 后端API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

### 启动后端服务

```bash
cd public/backend
java -jar mengyo-backend-1.0.0.jar --spring.profiles.active=prod
```

## 注意事项

1. 编译前请确保所有依赖已正确安装
2. 生产环境部署前请修改配置文件中的数据库、Redis等配置
3. 建议使用进程管理工具（如 PM2、Supervisor）管理后端服务
4. 定期备份数据库数据
