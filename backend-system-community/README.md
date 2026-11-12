# 社区民情收集平台 · 后端（你负责：系统管理 第6节起）

## 你做了什么

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 登录（临时） | `/api/auth` | 方便联调，同事正式实现后可合并 |
| 组织管理 | `/api/system/org` | 树形 CRUD |
| 用户管理 | `/api/system/user` | 分页 / CRUD / 批量删 |
| 数据字典 | `/api/system/dict` | 分页 / CRUD |
| 日志管理 | `/api/system/log` | 分页查询；`SysLogService` 供其它模块写日志 |

## 环境要求

- JDK 17
- MySQL 8
- IDEA（推荐，自带 Maven）或命令行 Maven

## 第一步：建库

用 Navicat / MySQL Workbench / 命令行执行：

```
backend/src/main/resources/db/schema.sql
```

## 第二步：改数据库账号密码

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    username: root
    password: 你的密码
```

## 第三步：用 IDEA 启动

1. **File → Open** 打开 `backend` 目录（选 pom.xml 以 Maven 工程打开）
2. 等待依赖下载完成
3. 运行 `CommunityApplication`
4. 控制台出现 `Started CommunityApplication` 即成功（默认端口 **8080**）

## 第四步：联调自测

默认账号：`admin` / `123456`

### 1）登录拿 token

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{"username":"admin","password":"123456"}
```

从返回的 `data.token` 复制 JWT。

### 2）带 token 调组织树

```http
GET http://localhost:8080/api/system/org
Authorization: Bearer 粘贴token
```

### 3）对接管理端前端

1. 管理端把登录从 mock 改为调用 `/api/auth/login`
2. `vite` 已代理 `/api` → `8080`
3. 打开「组织 / 用户 / 字典 / 日志」四个菜单验证

## 权限说明

- `/api/system/**` 仅 **admin** 角色可访问
- staff 访问会返回 HTTP 403

## 给同事对接

写日志请注入：

```java
sysLogService.saveLoginLog(operator, ip);
sysLogService.saveOperationLog(operator, module, content, ip);
```

## 目录结构

```
backend/
├── pom.xml
├── src/main/java/com/community/
│   ├── CommunityApplication.java
│   ├── common/          # Result、分页、异常
│   ├── config/          # Security、MyBatis、启动初始化
│   ├── security/        # JWT
│   ├── system/          # 系统管理（你的核心）
│   └── util/
└── src/main/resources/
    ├── application.yml
    └── db/schema.sql
```

## 建议操作顺序（跟做）

1. ✅ 工程已生成（当前）
2. ⬜ 执行 schema.sql
3. ⬜ 修改 yml 数据库密码
4. ⬜ IDEA 启动成功
5. ⬜ Postman 登录 + 组织树
6. ⬜ 联调前端系统管理四个页面
