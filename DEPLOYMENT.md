# 教育培训系统 - 生产部署文档

## 版本信息

| 项 | 值 |
|---|---|
| 系统名称 | 教育培训资源管理平台 |
| 版本 | v1.0.0 |
| 最后更新 | 2026-07-27 |
| Java版本 | 17+ |
| Spring Boot | 3.2.0 |
| 数据库 | MySQL 8.0+ |
| 缓存 | Redis 6.0+ |
| Node.js | 18+ (前端构建) |

---

## 一、服务器要求

### 1.1 最低配置（100人以内）

| 资源 | 配置 |
|------|------|
| CPU | 2核 |
| 内存 | 4GB |
| 硬盘 | 50GB SSD |
| 带宽 | 5Mbps |

### 1.2 推荐配置（500人以内）

| 资源 | 配置 |
|------|------|
| CPU | 4核 |
| 内存 | 8GB |
| 硬盘 | 100GB SSD |
| 带宽 | 10Mbps |

### 1.3 软件依赖

- **操作系统**：CentOS 7+/Ubuntu 20.04+/Windows Server 2019+
- **JDK**：OpenJDK 17 或 Oracle JDK 17
- **MySQL**：8.0+ (需开启InnoDB引擎)
- **Redis**：6.0+
- **Nginx**：1.18+ (反向代理和静态资源)
- **微信小程序**：需配置HTTPS域名

---

## 二、环境准备

### 2.1 JDK 17 安装

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk -y

# CentOS/RHEL
sudo yum install java-17-openjdk java-17-openjdk-devel -y

# 验证
java -version
```

### 2.2 MySQL 8.0 安装与配置

```bash
# Ubuntu/Debian
sudo apt install mysql-server-8.0 -y

# 启动并设置开机自启
sudo systemctl start mysql
sudo systemctl enable mysql

# 安全初始化
sudo mysql_secure_installation
```

**MySQL配置优化（my.cnf）：**

```ini
[mysqld]
# 基础设置
port=3306
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
default-storage-engine=INNODB
max_connections=200

# InnoDB优化
innodb_buffer_pool_size=1G
innodb_log_file_size=256M
innodb_flush_log_at_trx_commit=2
innodb_flush_method=O_DIRECT

# 日志
slow_query_log=ON
slow_query_log_file=/var/log/mysql/slow.log
long_query_time=2

# 时区
default-time-zone='+08:00'
```

**创建数据库和用户：**

```sql
CREATE DATABASE edu_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'edu_user'@'%' IDENTIFIED BY '你的强密码_ChangeMe!2026';
GRANT ALL PRIVILEGES ON edu_system.* TO 'edu_user'@'%';
FLUSH PRIVILEGES;
```

### 2.3 Redis 安装

```bash
# Ubuntu/Debian
sudo apt install redis-server -y

# 配置Redis密码和持久化
sudo sed -i 's/^supervised .*/supervised systemd/' /etc/redis/redis.conf
sudo sed -i 's/^# requirepass .*/requirepass 你的Redis密码_ChangeMe!/' /etc/redis/redis.conf
sudo sed -i 's/^appendonly .*/appendonly yes/' /etc/redis/redis.conf

sudo systemctl restart redis-server
sudo systemctl enable redis-server
```

### 2.4 Nginx 安装

```bash
# Ubuntu/Debian
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

---

## 三、数据库初始化

### 3.1 导入数据库脚本

```bash
# 1. 找到最新的全量SQL脚本
# 路径：education-store-server/sql/exports/edu-system-full.sql

# 2. 导入
mysql -u edu_user -p edu_system < edu-system-full.sql

# 验证
mysql -u edu_user -p -e "USE edu_system; SHOW TABLES;"
```

### 3.2 数据库迁移脚本

如需增量更新，请按顺序执行 `sql/` 目录下的 `migration-*.sql` 文件。

---

## 四、后端服务部署

### 4.1 构建后端Jar包

在项目根目录执行：

```bash
cd education-store-server

# Maven构建
mvn clean package -DskipTests -Pprod

# 产物位置
# edu-server-app/target/edu-server-app-1.0.0-SNAPSHOT.jar
```

### 4.2 创建应用目录

```bash
sudo mkdir -p /opt/edu-server/{logs,upload}
sudo cp edu-server-app-1.0.0-SNAPSHOT.jar /opt/edu-server/
sudo chown -R www-data:www-data /opt/edu-server  # Ubuntu
# 或
sudo chown -R nginx:nginx /opt/edu-server  # CentOS
```

### 4.3 配置环境变量

创建配置文件 `/opt/edu-server/.env`：

```bash
# 服务器配置
SERVER_PORT=8080

# 数据库配置
DB_HOST=127.0.0.1
DB_PORT=3306
DB_USERNAME=edu_user
DB_PASSWORD=你的强密码_ChangeMe!2026

# Redis配置
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
REDIS_PASSWORD=你的Redis密码_ChangeMe!

# JWT密钥（必须修改，至少64位随机字符串）
JWT_SECRET=请使用openssl rand -hex 32生成并填入此处_SuperLongSecretKeyForProduction2026!@#

# CORS允许的前端域名（生产环境域名，逗号分隔）
CORS_ALLOWED_ORIGINS=https://admin.yourdomain.com,https://parent.yourdomain.com

# 文件上传路径
EDU_UPLOAD_PATH=/opt/edu-server/upload

# 默认密码（生产环境务必修改）
EDU_USER_DEFAULT_PASSWORD=你的初始密码_ChangeMe!2026

# 微信小程序配置（如使用）
WECHAT_APP_ID=你的微信小程序AppID
WECHAT_APP_SECRET=你的微信小程序AppSecret
```

### 4.4 创建Systemd服务

创建文件 `/etc/systemd/system/edu-server.service`：

```ini
[Unit]
Description=Education Training System Server
After=network.target mysql.service redis.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/edu-server
EnvironmentFile=/opt/edu-server/.env
ExecStart=/usr/bin/java -Xms512m -Xmx2g -jar edu-server-app-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
Restart=always
RestartSec=10
StandardOutput=journal+console
StandardError=journal+console

[Install]
WantedBy=multi-user.target
```

**JVM参数说明：**
- `-Xms512m`：初始堆内存512MB
- `-Xmx2g`：最大堆内存2GB（根据服务器内存调整）
- 如需4GB内存可设为 `-Xms1g -Xmx4g`

### 4.5 启动服务

```bash
sudo systemctl daemon-reload
sudo systemctl start edu-server
sudo systemctl enable edu-server

# 查看状态
sudo systemctl status edu-server

# 查看日志
sudo journalctl -u edu-server -f
```

### 4.6 验证服务启动

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 应返回：{"status":"UP"}
```

---

## 五、前端管理后台部署

### 5.1 构建前端

```bash
cd education-store-web

# 安装依赖
npm install
# 或
pnpm install

# 构建生产版本
pnpm build

# 产物位置：dist/ 目录
```

**配置API地址：**

编辑 `.env.production`：

```env
VITE_API_BASE_URL=https://api.yourdomain.com/api/v1
VITE_APP_TITLE=教育培训管理系统
```

### 5.2 部署到Nginx

```bash
sudo mkdir -p /var/www/edu-admin
sudo cp -r dist/* /var/www/edu-admin/
sudo chown -R www-data:www-data /var/www/edu-admin
```

---

## 六、微信小程序部署

### 6.1 配置小程序

在微信开发者工具中打开 `education-store-mini/` 目录：

1. 修改 `src/utils/request.js` 中的API地址为生产域名
2. 在微信公众平台配置服务器域名（request合法域名）：`https://api.yourdomain.com`
3. 配置uploadFile合法域名：`https://api.yourdomain.com`
4. 上传代码并提交审核

### 6.2 微信订阅消息配置

在微信公众平台配置订阅消息模板，并将模板ID填入后端配置：

```bash
# .env中添加
WECHAT_SUBSCRIBE_SIGN_IN=模板ID
WECHAT_SUBSCRIBE_HOMEWORK=模板ID
WECHAT_SUBSCRIBE_LEAVE=模板ID
WECHAT_SUBSCRIBE_EVALUATION=模板ID
WECHAT_SUBSCRIBE_REDEMPTION=模板ID
```

---

## 七、Nginx配置（HTTPS）

### 7.1 申请SSL证书

推荐使用Let's Encrypt免费证书：

```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d api.yourdomain.com -d admin.yourdomain.com
```

### 7.2 Nginx配置示例

创建 `/etc/nginx/sites-available/edu-server`：

```nginx
# 后端API服务
upstream edu_backend {
    server 127.0.0.1:8080;
    keepalive 32;
}

# HTTP重定向HTTPS
server {
    listen 80;
    server_name api.yourdomain.com admin.yourdomain.com;
    return 301 https://$host$request_uri;
}

# API服务
server {
    listen 443 ssl http2;
    server_name api.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/api.yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.yourdomain.com/privkey.pem;

    # SSL优化
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # 文件上传大小
    client_max_body_size 60m;

    # 反向代理到后端
    location / {
        proxy_pass http://edu_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        proxy_connect_timeout 300s;
        proxy_send_timeout 300s;
        proxy_read_timeout 300s;
    }

    # 上传文件安全防护 - Nginx层二次校验
    location ~* /upload/.*\.(jsp|jspx|php|asp|aspx|exe|sh|bat|cmd|html|htm)$ {
        deny all;
        return 403;
    }

    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|webp|pdf|doc|docx|xls|xlsx)$ {
        proxy_pass http://edu_backend;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}

# 管理后台前端
server {
    listen 443 ssl http2;
    server_name admin.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/admin.yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/admin.yourdomain.com/privkey.pem;

    root /var/www/edu-admin;
    index index.html;

    # SPA路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Gzip压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript image/svg+xml;
    gzip_min_length 1024;
}
```

启用配置：

```bash
sudo ln -s /etc/nginx/sites-available/edu-server /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

## 八、防火墙配置

### 8.1 UFW防火墙（Ubuntu）

```bash
sudo ufw allow 22/tcp      # SSH
sudo ufw allow 80/tcp      # HTTP
sudo ufw allow 443/tcp     # HTTPS
sudo ufw enable
```

**注意**：禁止直接暴露8080端口到公网，只允许本地访问。

### 8.2 firewalld（CentOS）

```bash
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

---

## 九、日志管理

### 9.1 日志位置

| 日志类型 | 路径 |
|----------|------|
| 应用日志 | `/opt/edu-server/logs/edu-server.log` |
| 访问日志 | `/var/log/nginx/access.log` |
| 错误日志 | `/var/log/nginx/error.log` |
| MySQL慢查询 | `/var/log/mysql/slow.log` |
| Systemd日志 | `journalctl -u edu-server` |

### 9.2 日志轮转配置

创建 `/etc/logrotate.d/edu-server`：

```
/opt/edu-server/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0640 www-data www-data
    sharedscripts
    postrotate
        systemctl reload edu-server > /dev/null 2>&1 || true
    endscript
}
```

---

## 十、备份策略

### 10.1 数据库每日备份

创建备份脚本 `/opt/edu-server/backup-db.sh`：

```bash
#!/bin/bash
BACKUP_DIR="/opt/edu-server/backups/db"
DATE=$(date +%Y%m%d_%H%M%S)
KEEP_DAYS=30

mkdir -p $BACKUP_DIR

# 数据库备份
mysqldump -u edu_user -p'你的强密码_ChangeMe!2026' \
    --single-transaction \
    --routines \
    --triggers \
    edu_system > $BACKUP_DIR/edu_system_$DATE.sql

# 压缩
gzip $BACKUP_DIR/edu_system_$DATE.sql

# 清理旧备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +$KEEP_DAYS -delete

# 上传文件备份
tar -czf $BACKUP_DIR/upload_$DATE.tar.gz -C /opt/edu-server upload
find $BACKUP_DIR -name "upload_*.tar.gz" -mtime +$KEEP_DAYS -delete
```

添加执行权限并设置定时任务：

```bash
chmod +x /opt/edu-server/backup-db.sh

# 编辑crontab
crontab -e

# 添加：每天凌晨3点执行备份
0 3 * * * /opt/edu-server/backup-db.sh >> /opt/edu-server/logs/backup.log 2>&1
```

---

## 十一、健康检查与监控

### 11.1 健康检查端点

- `GET https://api.yourdomain.com/actuator/health` - 应用健康状态

### 11.2 监控脚本示例

```bash
#!/bin/bash
HEALTH_URL="https://api.yourdomain.com/actuator/health"
ALERT_EMAIL="a****@***********"

RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_URL)

if [ $RESPONSE -ne 200 ]; then
    echo "服务异常！HTTP状态码：$RESPONSE" | mail -s "【警报】教育培训系统服务异常" $ALERT_EMAIL
    # 自动重启
    systemctl restart edu-server
fi
```

---

## 十二、常见问题排查

### 12.1 服务无法启动

```bash
# 查看详细日志
journalctl -u edu-server -n 100 --no-pager

# 常见原因：
# 1. 数据库连接失败 → 检查DB_HOST/DB_PASSWORD/MySQL是否启动
# 2. Redis连接失败 → 检查Redis配置
# 3. JWT密钥太短 → 确保至少64字符
# 4. 端口被占用 → 检查8080端口
```

### 12.2 文件上传失败

1. 检查 `/opt/edu-server/upload` 目录权限
2. 检查Nginx `client_max_body_size` 配置
3. 检查磁盘空间 `df -h`

### 12.3 微信小程序无法请求

1. 确认服务器域名已在微信公众平台配置
2. 确认使用HTTPS
3. 检查SSL证书是否有效

---

## 十三、回滚方案

如新版本出现问题，按以下步骤回滚：

```bash
# 1. 停止当前服务
sudo systemctl stop edu-server

# 2. 备份当前版本
sudo cp /opt/edu-server/edu-server-app-1.0.0-SNAPSHOT.jar /opt/edu-server/edu-server-app-1.0.0-SNAPSHOT.jar.bak

# 3. 替换为旧版本Jar包
sudo cp /path/to/old-version.jar /opt/edu-server/edu-server-app-1.0.0-SNAPSHOT.jar

# 4. 如需回滚数据库，请从备份恢复
# mysql -u edu_user -p edu_system < backup_file.sql.gz

# 5. 启动服务
sudo systemctl start edu-server
```

---

## 十四、环境变量清单

| 变量名 | 必填 | 说明 | 示例 |
|--------|------|------|------|
| `DB_HOST` | 是 | MySQL地址 | `127.0.0.1` |
| `DB_PORT` | 是 | MySQL端口 | `3306` |
| `DB_USERNAME` | 是 | 数据库用户名 | `edu_user` |
| `DB_PASSWORD` | 是 | 数据库密码 | 强密码 |
| `REDIS_HOST` | 是 | Redis地址 | `127.0.0.1` |
| `REDIS_PORT` | 是 | Redis端口 | `6379` |
| `REDIS_PASSWORD` | 否 | Redis密码 | 如配置需填写 |
| `JWT_SECRET` | 是 | JWT密钥 | 至少64位随机字符串 |
| `CORS_ALLOWED_ORIGINS` | 是 | CORS白名单 | `https://admin.xxx.com` |
| `EDU_UPLOAD_PATH` | 否 | 上传目录 | `/opt/edu-server/upload` |
| `EDU_USER_DEFAULT_PASSWORD` | 否 | 默认密码 | 建议修改 |
| `WECHAT_APP_ID` | 否 | 微信AppID | 小程序功能需要 |
| `WECHAT_APP_SECRET` | 否 | 微信AppSecret | 小程序功能需要 |

---

**文档结束 - 最后更新：2026-07-27**
