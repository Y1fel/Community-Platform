# Community Platform

社区民情收集与投诉处理平台，包含居民端、管理端和后端。

## 项目结构

| 目录 | 内容 |
| --- | --- |
| `frontend/` | 居民端：Vue 3、Vant、Vite |
| `untitled/` | 管理端：Vue 3、Element Plus、Pinia、Vite |
| `backend-system-community/` | 后端：Java 17、Spring Boot、MyBatis Plus |
| `docker/` | 前后端镜像构建与 Nginx 配置 |
| `docker-compose.yml` | MySQL、后端和 Web 服务编排 |

## Docker 部署

1. 安装 Docker 和 Docker Compose。
2. 将 `.env.example` 复制为 `.env`，设置独立的数据库密码和随机 JWT 密钥。
3. 在项目根目录执行 `docker compose up -d --build`。
4. 管理端入口为 `/`，居民端入口为 `/h5/`，接口入口为 `/api/`。

详细部署、初始化与更新说明见 [Docker部署说明.md](Docker部署说明.md)。
现有后端包含启动时重置管理员密码的初始化逻辑，上线前请按部署说明处理。

## 本地开发

- 后端使用 Java 17，可通过 `backend-system-community/mvnw` 启动。
- 两个前端分别在各自目录运行 `npm ci`、`npm run dev`；管理端要求 Node.js 22.18+ 或符合其 `package.json` 的较新版本。
- 本地 H2 配置会执行初始化脚本，仅用于开发环境。

## 仓库内容

本仓库保留源码、依赖锁定文件、数据库建表与示例初始化脚本、部署配置及项目文档。
服务器 `.env`、线上数据库备份、运行数据、`node_modules`、编译产物和下载压缩包不纳入版本管理。
