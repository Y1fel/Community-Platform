# 社区民情收集平台 Docker 部署说明

> 本文只说明部署、检查、更新和回滚流程，不修改业务代码、前端代码或 `application.yml`。
>
> 当前项目由三个运行部分组成：`backend-system-community`（Spring Boot 后端）、`frontend`（居民端）和 `untitled`（管理端）。部署时三者必须使用同一版本的发布包。Docker 部署后的固定路径为：管理端 `/`，居民端 `/h5/`。

## 1. 部署前确认

在服务器上确认 Docker 已安装：

```bash
docker --version
docker compose version
```

准备一个专用目录，例如：

```bash
sudo mkdir -p /opt/community-platform
sudo chown "$USER":"$USER" /opt/community-platform
cd /opt/community-platform
```

Docker 部署所需文件已在项目中提供：

```text
docker-compose.yml
.env.example
.dockerignore
docker/backend/Dockerfile
docker/web/Dockerfile
docker/nginx/default.conf
```

首次部署先创建真实环境变量文件，替换全部密码和 JWT 密钥：

```bash
cp .env.example .env
chmod 600 .env
```

不要将 `.env` 提交到代码仓库，也不要上传本地开发数据库目录（例如 `backend-system-community/data`）、`node_modules` 或源码编译缓存。

建议在启动前检查部署目录实际包含的 Compose 服务：

```bash
docker compose config --services
docker compose config
```

`docker compose config` 能展开环境变量并校验 Compose 文件；它只读取配置，不会启动或修改容器。

> 当前源码中的 `AdminPasswordInitializer` 会在每次后端启动时把 `admin` 密码重置为 `123456`。这是现有业务代码的行为，Docker 配置无法安全地覆盖；本次未改动该代码。首次启动后请立即处理该上线风险，且不要把默认管理员账号暴露到公网。

## 2. 首次启动

在 Docker Compose 文件所在目录执行：

```bash
docker compose pull
docker compose up -d
docker compose ps
```

首次启动后观察日志，直到服务稳定：

```bash
docker compose logs -f --tail=100
```

按 `Ctrl+C` 只会退出日志查看，不会停止容器。

## 3. 验证顺序

先检查容器状态：

```bash
docker compose ps
```

所有核心服务应为 `running` 或 `healthy`。若某服务反复重启，先查看它自己的日志：

```bash
docker compose logs --tail=200 <服务名>
```

再从服务器本机验证公网入口（按实际域名或端口替换）：

```bash
curl -I http://127.0.0.1/
curl -i http://127.0.0.1/api/dashboard/summary
```

`/api/dashboard/summary` 在未登录时返回 HTTP `401` 是正常结果，说明反向代理已到达后端；连接失败、`502` 或 `5xx` 才需要排查。

最后用浏览器验证：

1. 管理端：`http://124.220.9.178/`，能打开并正常登录。
2. 居民端：`http://124.220.9.178/h5/`，能打开上报页面。
3. 居民端提交一条工单后，管理端能看到并处理。
4. 重启后仍能登录并看到刚才的数据。

## 4. 数据库与首次初始化

本项目的默认开发配置是 H2，并且包含会删表后重新建表的初始化脚本。因此生产 Docker 部署必须遵守以下规则：

- 使用 Compose 中配置的持久化数据库卷或外部 MySQL；数据库数据不能只存在容器可写层。
- 初始化建表脚本只允许在**全新的空数据库**首次启动时执行一次。
- 生产重启不得再次执行 `schema.sql`、`schema-h2.sql`、`seed-h2.sql`。
- 后端生产启动参数应禁用 SQL 初始化，即 `spring.sql.init.mode=never`；H2 控制台也应关闭。
- 不要执行 `docker compose down -v`。该命令会移除 Compose 管理的数据卷，数据库数据通常无法恢复。

若需要确认数据库是否被持久化，可先记下一条测试数据，执行：

```bash
docker compose restart
```

然后重新登录核对数据仍在。确认前不要做升级或清理操作。

## 5. 日常运维命令

查看状态：

```bash
docker compose ps
```

查看全部日志：

```bash
docker compose logs -f --tail=200
```

重启全部服务（数据库数据卷不会因此删除）：

```bash
docker compose restart
```

仅重启某个服务：

```bash
docker compose restart <服务名>
```

停止服务、但保留容器卷和数据库：

```bash
docker compose down
```

重新启动：

```bash
docker compose up -d
```

## 6. 发布更新

发布前先保留上一版部署包，并备份数据库。确认备份文件可读取后，再上传新版本后端 JAR 和两个前端构建产物。

更新流程：

```bash
cd /opt/community-platform
docker compose config
docker compose up -d
docker compose ps
docker compose logs -f --tail=100
```

若 Compose 配置通过镜像构建应用，使用：

```bash
docker compose up -d --build
```

不要在不确认 Compose 配置内容时增加 `-v`、`--renew-anon-volumes` 或数据库初始化参数。

## 7. 回滚

回滚前不要删除当前数据库卷。将后端 JAR、居民端构建产物和管理端构建产物同时恢复为上一版，然后执行：

```bash
cd /opt/community-platform
docker compose up -d
docker compose ps
```

若更新包含数据库结构变更，先恢复对应的数据库备份，再启动旧版本；仅回滚代码而不处理不兼容的数据库结构，可能导致旧版本无法运行。

## 8. 常见问题

### 页面能打开，但接口返回 502

先检查后端容器是否运行：

```bash
docker compose ps
docker compose logs --tail=200 <后端服务名>
```

常见原因是后端无法连接数据库、JAR 路径错误，或反向代理指向的服务名/端口不一致。

### 管理端正常，居民端接口请求失败

管理端请求路径固定为 `/api`；居民端由 `VITE_API_BASE_URL` 在构建时写入。确认居民端发布时的 API 地址指向当前域名下的 `/api`，随后重新构建并替换居民端产物。

### 重启后数据消失

立即停止继续初始化，检查：

```bash
docker compose config
docker volume ls
docker compose logs --tail=300
```

重点确认数据库是否挂载了命名卷或宿主机目录，以及后端是否错误启用了 SQL 初始化。不要重新执行初始化 SQL。

### 忘记管理员密码

不要通过删除数据库、重跑种子数据或重建容器来处理。应在确认数据库备份存在后，通过明确的管理员密码重置流程操作；避免每次后端重启自动覆盖管理员密码。

## 9. 需要避免的命令

```bash
# 会删除 Compose 管理的数据卷，生产环境禁止执行
docker compose down -v

# 会清理所有未被引用的 Docker 数据卷，生产环境不要随意执行
docker volume prune

# 会删除所有已停止容器和未使用资源，执行前必须人工确认范围
docker system prune
```

部署或故障处理时，先执行 `docker compose ps` 和 `docker compose logs` 获取事实，再决定是否重启、更新或回滚。
