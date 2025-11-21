# 萌友益站 - 后端服务

## 项目简介

萌友益站是一个专为喜爱小动物、关爱流浪动物的人士打造的公益救助平台后端服务。

## 技术栈

- **核心框架**: Spring Boot 3.2.0
- **持久层**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **API文档**: Knife4j (Swagger 3)
- **JWT**: JJWT 0.12.3
- **工具库**: Hutool 5.8.23

## 项目结构

```
mengyo-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── mengyo/
│   │   │           ├── MengyoApplication.java       # 启动类
│   │   │           ├── common/                       # 公共模块
│   │   │           │   ├── constant/                # 常量定义
│   │   │           │   ├── entity/                  # 基础实体
│   │   │           │   ├── exception/               # 异常处理
│   │   │           │   ├── result/                  # 统一返回
│   │   │           │   └── utils/                   # 工具类
│   │   │           ├── config/                      # 配置类
│   │   │           │   ├── MybatisPlusConfig.java  # MyBatis配置
│   │   │           │   ├── RedisConfig.java         # Redis配置
│   │   │           │   ├── Knife4jConfig.java       # Swagger配置
│   │   │           │   └── MetaObjectHandlerConfig.java # 自动填充
│   │   │           └── module/                      # 业务模块
│   │   │               └── user/                    # 用户模块
│   │   │                   ├── controller/
│   │   │                   ├── service/
│   │   │                   ├── mapper/
│   │   │                   └── entity/
│   │   └── resources/
│   │       ├── application.yml                      # 主配置
│   │       ├── application-dev.yml                  # 开发环境
│   │       └── application-prod.yml                 # 生产环境
│   └── test/
├── pom.xml
└── README.md
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 2. 数据库初始化

```bash
# 执行数据库初始化脚本
mysql -u root -p < database/init.sql
```

### 3. 配置文件

修改 `application-dev.yml` 中的数据库和Redis连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mengyo_db
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_password
```

### 4. 启动项目

```bash
# 方式1: Maven命令
mvn spring-boot:run

# 方式2: IDE运行
直接运行 MengyoApplication.java
```

### 5. 访问API文档

启动成功后，访问：http://localhost:8080/api/doc.html

## 核心功能模块

### 已实现
- ✅ 统一响应格式
- ✅ 全局异常处理
- ✅ MyBatis-Plus集成和配置
- ✅ Redis缓存配置
- ✅ Swagger API文档
- ✅ JWT认证工具类
- ✅ 公共工具类封装

### 开发中
- 🔄 用户注册/登录
- 🔄 用户认证授权
- 🔄 流浪动物救助模块
- 🔄 领养中心模块
- 🔄 捐赠模块
- 🔄 社区交流模块

## 接口规范

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

| 状态码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |
| 1xxx | 认证授权相关 |
| 2xxx | 用户相关 |
| 3xxx | 动物救助相关 |
| 4xxx | 领养相关 |
| 5xxx | 捐赠相关 |
| 6xxx | 社区相关 |

### 请求Header

```
Authorization: Bearer {token}
Content-Type: application/json
```

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发规范
- 类名使用大驼峰命名（PascalCase）
- 方法名、变量名使用小驼峰命名（camelCase）
- 常量使用全大写下划线分隔（UPPER_SNAKE_CASE）
- 必须添加类和方法注释

### Git提交规范
```
[feat] 新功能
[fix] 修复bug
[docs] 文档更新
[style] 代码格式调整
[refactor] 代码重构
[test] 测试相关
[chore] 构建/工具变动
```

## 配置说明

### JWT配置

```yaml
jwt:
  secret: your-secret-key          # 密钥（生产环境必须修改）
  expiration: 604800               # 过期时间（秒），默认7天
  token-prefix: Bearer             # Token前缀
  header: Authorization            # Header key
```

### 文件上传配置

```yaml
file:
  upload:
    path: /data/mengyo/upload/     # 本地存储路径
    url-prefix: http://localhost:8080/api/files/  # 访问URL前缀
```

## 部署说明

### 打包

```bash
mvn clean package -DskipTests
```

### 运行

```bash
java -jar target/mengyo-backend-1.0.0.jar --spring.profiles.active=prod
```

### Docker部署

```bash
# 构建镜像
docker build -t mengyo-backend:1.0.0 .

# 运行容器
docker run -d -p 8080:8080 \
  -e DB_HOST=mysql \
  -e DB_PORT=3306 \
  -e DB_NAME=mengyo_db \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e REDIS_HOST=redis \
  -e REDIS_PORT=6379 \
  mengyo-backend:1.0.0
```

## 监控和日志

### Druid监控

开发环境访问：http://localhost:8080/api/druid/index.html

- 用户名：admin
- 密码：admin

### 日志配置

- 开发环境日志：`logs/mengyo-dev.log`
- 生产环境日志：`/var/log/mengyo/mengyo-prod.log`

## 常见问题

### 1. 数据库连接失败

检查MySQL是否启动，配置文件中的连接信息是否正确。

### 2. Redis连接失败

检查Redis是否启动，防火墙是否开放端口。

### 3. Token验证失败

检查JWT密钥配置是否正确，Token是否过期。

## 联系我们

- 项目地址：https://github.com/mengyo/mengyo-backend
- 问题反馈：https://github.com/mengyo/mengyo-backend/issues
- 邮箱：support@mengyo.com

## 开源协议

本项目采用 Apache 2.0 开源协议

---

**版本**: v1.0.0  
**最后更新**: 2025-11-21
