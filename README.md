# 校园抢单助手

单校园的跑腿抢单平台。学生发单、骑手抢单、管理员看板，一套微信小程序按角色切换。

技术栈：Spring Boot 3 + MyBatis-Plus + MySQL 8 + Redis 7 + 原生微信小程序，Docker Compose 部署。

本仓库当前是**第一周骨架**，目标只有一个：手机小程序按一下按钮，本机后端返回一句话。这条链路通了，后面所有业务都是往里填内容。

---

## 目录结构

```
campus-errand/
├── docker-compose.yml        # 一条命令拉起 MySQL + Redis
├── .env.example              # 复制成 .env 再用
├── pom.xml                   # Maven 依赖
├── sql/schema.sql            # 5 张表，容器首次启动自动执行
├── src/main/
│   ├── resources/application.yml
│   └── java/com/campus/errand/
│       ├── ErrandApplication.java     # 启动类
│       ├── common/                    # Result 统一返回体、异常处理
│       └── module/ping/               # ping 连通测试接口
└── miniprogram/              # 微信小程序，当前只有 ping 页
```

后端按**业务模块**分包（module/ping、后面加 module/user、module/task），不是按 controller/service/mapper 三大层分。业务一多，同层分包要在三个目录间来回跳；按模块分，一个功能的东西都在一起。

---

## 一次性准备（只做一遍）

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven git
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
```

最后一条执行完要**注销重新登录**（或重启），docker 免 sudo 才生效。验证：

```bash
java -version              # 应显示 17
mvn -version              # 应显示 3.6+
docker compose version    # 应显示 v2.x
```

---

## 跑起来（四步）

第一步，准备环境变量：

```bash
cp .env.example .env
```

第二步，拉起数据库和缓存。第一次会拉镜像，慢一点正常：

```bash
docker compose up -d
```

等 MySQL 就绪（它初始化要二三十秒，急着连会连不上）：

```bash
until docker exec errand-mysql mysqladmin ping -uroot -proot123456 --silent 2>/dev/null; do
  echo "等 MySQL 起来..."; sleep 2
done
echo "MySQL 就绪"
```

顺手确认表建好了、redis 活着：

```bash
docker exec -it errand-mysql mysql -uroot -proot123456 -e "use errand; show tables;"
docker exec -it errand-redis redis-cli ping        # 返回 PONG
```

第三步，启动后端。第一次 mvn 会下依赖，耐心等：

```bash
mvn spring-boot:run
```

看到 `Started ErrandApplication` 就是成功了。这个终端会一直被占用，别关。

第四步，另开一个终端，用 curl 验证：

```bash
curl http://127.0.0.1:8080/api/ping
# 期望：{"code":0,"msg":"ok","data":{"msg":"pong","time":...}}
```

curl 通了，后端这半条链路就成立了。

---

## 接上小程序

1. 下载并安装「微信开发者工具」（Windows/Mac，Ubuntu 可在另一台机器或虚拟机里开）。
2. 打开工具 → 导入项目 → 目录选 `miniprogram/` → AppID 先点「测试号」。
3. 项目里已经把「不校验合法域名」设好了（`project.config.json` 的 `urlCheck: false`），不用手动勾。
4. 编译后点页面上「测试后端连接」，看到「连通成功」即闭环打通。

**真机调试**：手机和电脑连同一个 WiFi，先看电脑局域网 IP：

```bash
ip addr | grep 'inet 192'      # 找形如 192.168.x.x 的地址
```

把 `miniprogram/app.js` 里的 `baseUrl` 从 `127.0.0.1` 改成这个 IP，再在开发者工具点「真机调试」扫码。手机上点按钮能连通，就算全链路跑通了。

---

## 常见报错

- **后端启动报 `Communications link failure` / 连不上数据库**：MySQL 还没起来或没起成功。先跑上面那段 `until ... mysqladmin ping` 等待，再启动后端。
- **小程序报 `不在以下 request 合法域名列表中`**：`urlCheck` 没生效，去开发者工具「详情 → 本地设置」手动勾上「不校验合法域名」。
- **改了 `.env` 里的密码后连不上**：`application.yml` 的密码默认 `root123456`，改密码要两边一致，或删掉 `data/mysql` 目录让容器重新初始化。
- **端口被占**：`3306`、`6379`、`8080` 被占就改 `docker-compose.yml` 和 `application.yml` 里的端口。

---

## 停止与清理

```bash
docker compose down          # 停容器，数据保留
docker compose down -v       # 停容器并删数据卷（下次重新建表）
rm -rf data/                 # 彻底清掉本地数据
```

---

## 路线图

- 第 1 周 ✅ 骨架 + ping 闭环（当前）
- 第 2 周 微信登录（code2session 换 openid + JWT）、任务发布与列表
- 第 3 周 抢单三级演进（先查后改 → 数据库 CAS → Redis Lua 前置拦截）+ JMeter 压测复现超卖
- 第 4 周 状态机 + 状态日志、超时回池、WebSocket 实时推送
- 第 5 周 前端各页面、数据看板、录演示视频、写踩坑记录

---

## 注意

本项目从零自写。上传的那套商业外卖破解包只用于理解设计，任何代码、SQL、证书、用户数据都不进本仓库。数据库密码、微信 AppSecret 一律走环境变量，`.env` 已在 `.gitignore` 里。
