# 运维监控手册

**项目：教育培训资源管理平台**
**版本：v1.0.0**
**最后更新：2026-07-27**

---

## 一、日常运维操作

### 1.1 服务启停命令

```bash
# 启动服务
sudo systemctl start edu-server

# 停止服务
sudo systemctl stop edu-server

# 重启服务
sudo systemctl restart edu-server

# 查看服务状态
sudo systemctl status edu-server

# 查看实时日志
sudo journalctl -u edu-server -f

# 查看最近100行日志
sudo journalctl -u edu-server -n 100 --no-pager

# 设置开机自启
sudo systemctl enable edu-server

# 取消开机自启
sudo systemctl disable edu-server
```

### 1.2 Nginx操作

```bash
# 检查配置
sudo nginx -t

# 重载配置（不中断服务）
sudo nginx -s reload

# 重启Nginx
sudo systemctl restart nginx

# 查看Nginx状态
sudo systemctl status nginx
```

### 1.3 MySQL操作

```bash
# 启动MySQL
sudo systemctl start mysql

# 停止MySQL
sudo systemctl stop mysql

# 重启MySQL
sudo systemctl restart mysql

# 连接MySQL
mysql -u edu_user -p edu_system

# 查看当前连接数
mysql -u edu_user -p -e "SHOW STATUS LIKE 'Threads_connected';" edu_system

# 查看慢查询
sudo tail -f /var/log/mysql/slow.log
```

### 1.4 Redis操作

```bash
# 启动Redis
sudo systemctl start redis

# 停止Redis
sudo systemctl stop redis

# 连接Redis
redis-cli -a 你的Redis密码

# 查看Redis信息
redis-cli -a 你的Redis密码 INFO

# 查看Redis内存使用
redis-cli -a 你的Redis密码 INFO memory
```

---

## 二、日志查看

### 2.1 日志位置汇总

| 服务 | 日志路径 | 说明 |
|------|---------|------|
| 后端应用 | `/opt/edu-server/logs/edu-server.log` | 应用运行日志 |
| 应用标准输出 | `journalctl -u edu-server` | Systemd日志 |
| Nginx访问日志 | `/var/log/nginx/access.log` | HTTP访问日志 |
| Nginx错误日志 | `/var/log/nginx/error.log` | Nginx错误日志 |
| MySQL错误日志 | `/var/log/mysql/error.log` | MySQL错误日志 |
| MySQL慢查询 | `/var/log/mysql/slow.log` | 慢SQL日志 |
| 备份日志 | `/opt/edu-server/logs/backup.log` | 数据库备份日志 |

### 2.2 常用日志查询命令

```bash
# 实时查看应用日志
tail -f /opt/edu-server/logs/edu-server.log

# 查看错误日志
grep ERROR /opt/edu-server/logs/edu-server.log | tail -50

# 查看特定时间段日志
grep "2026-07-27 14:" /opt/edu-server/logs/edu-server.log

# 统计今日访问量（Nginx）
awk '{print $1}' /var/log/nginx/access.log | sort | uniq -c | sort -rn | head -20

# 查看HTTP状态码统计
awk '{print $9}' /var/log/nginx/access.log | sort | uniq -c | sort -rn

# 查看500错误
grep " 500 " /var/log/nginx/access.log | tail -20

# 查看慢SQL
tail -f /var/log/mysql/slow.log
```

---

## 三、监控指标

### 3.1 系统资源监控

| 指标 | 正常范围 | 告警阈值 | 查看命令 |
|------|---------|---------|---------|
| CPU使用率 | <70% | >85%持续5分钟 | `top`/`htop` |
| 内存使用率 | <80% | >90% | `free -h` |
| 磁盘使用率 | <70% | >85% | `df -h` |
| 磁盘IO | <50% | >80%持续 | `iostat -x 1` |
| 系统负载 | < CPU核数*2 | > CPU核数*4 | `uptime` |

**快速查看系统状态：**
```bash
# 综合查看
top

# 内存
free -h

# 磁盘
df -h

# 负载
uptime

# 查看Java进程
ps aux | grep java
```

### 3.2 应用监控

| 指标 | 正常范围 | 告警阈值 |
|------|---------|---------|
| 应用状态 | Running | 非Running |
| JVM堆内存 | <70% | >85% |
| GC频率 | 正常 | Full GC频繁 |
| 活跃线程 | <200 | >500 |
| 数据库连接池 | 活跃<15 | 活跃>18 |
| 接口响应时间 | <500ms | P99>3s |
| 错误率 | <0.1% | >1% |

### 3.3 数据库监控

| 指标 | 正常范围 | 告警阈值 |
|------|---------|---------|
| 连接数 | <100 | >180 |
| 慢查询 | 0个 | >10个/小时 |
| 锁等待 | <1s | >5s |
| QPS | 根据业务 | 突增/突降 |

---

## 四、健康检查

### 4.1 手动健康检查

```bash
# 应用健康检查
curl -s http://localhost:8080/actuator/health
# 正常返回：{"status":"UP"}

# HTTP状态检查
curl -s -o /dev/null -w "%{http_code}" https://api.yourdomain.com/actuator/health
# 正常返回：200

# 前端页面检查
curl -s -o /dev/null -w "%{http_code}" https://admin.yourdomain.com
# 正常返回：200
```

### 4.2 自动健康检查脚本

创建 `/opt/edu-server/health-check.sh`：

```bash
#!/bin/bash
# 健康检查脚本，建议配置crontab每5分钟执行一次

HEALTH_URL="http://localhost:8080/actuator/health"
ALERT_WEBHOOK="你的企业微信/钉钉webhook地址"  # 可选
LOG_FILE="/opt/edu-server/logs/health-check.log"

# 检查健康端点
RESPONSE=$(curl -s -m 10 $HEALTH_URL)
HTTP_CODE=$(curl -s -o /dev/null -m 10 -w "%{http_code}" $HEALTH_URL)

if [ "$HTTP_CODE" != "200" ] || [[ ! "$RESPONSE" == *"UP"* ]]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 服务异常! HTTP_CODE=$HTTP_CODE RESPONSE=$RESPONSE" >> $LOG_FILE
    
    # 记录线程栈
    PID=$(pgrep -f edu-server-app)
    if [ -n "$PID" ]; then
        jstack $PID >> /opt/edu-server/logs/thread-dump-$(date +%Y%m%d_%H%M%S).log 2>/dev/null
    fi
    
    # 自动重启
    systemctl restart edu-server
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 已尝试重启服务" >> $LOG_FILE
    
    # 发送告警（配置webhook后启用）
    # curl -s -X POST $ALERT_WEBHOOK -H "Content-Type: application/json" -d "{\"content\":\"【警告】教育培训系统服务异常，已自动重启\"}"
else
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 服务正常" >> $LOG_FILE
fi

# 磁盘空间检查
DISK_USAGE=$(df / | grep / | awk '{print $5}' | sed 's/%//g')
if [ $DISK_USAGE -gt 85 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 警告：磁盘使用率 ${DISK_USAGE}%" >> $LOG_FILE
fi
```

设置定时执行：
```bash
chmod +x /opt/edu-server/health-check.sh

# crontab -e 添加：
# */5 * * * * /opt/edu-server/health-check.sh
```

---

## 五、备份与恢复

### 5.1 数据库备份

**手动备份：**
```bash
# 执行备份脚本
/opt/edu-server/backup-db.sh

# 或手动执行
mysqldump -u edu_user -p'你的密码' \
    --single-transaction \
    --routines \
    --triggers \
    edu_system | gzip > /opt/edu-server/backups/db/manual_backup_$(date +%Y%m%d_%H%M%S).sql.gz
```

### 5.2 数据恢复

```bash
# 1. 停止服务
sudo systemctl stop edu-server

# 2. 恢复数据库
gunzip < /opt/edu-server/backups/db/edu_system_20260727_030000.sql.gz | mysql -u edu_user -p edu_system

# 3. 如需恢复上传文件
tar -xzf /opt/edu-server/backups/db/upload_20260727_030000.tar.gz -C /

# 4. 启动服务
sudo systemctl start edu-server
```

### 5.3 备份文件清理

备份脚本已配置自动清理30天前的备份，也可手动清理：
```bash
# 清理60天前的备份
find /opt/edu-server/backups -name "*.sql.gz" -mtime +60 -delete
find /opt/edu-server/backups -name "upload_*.tar.gz" -mtime +60 -delete
```

---

## 六、版本更新与回滚

### 6.1 版本更新步骤

```bash
# 1. 备份数据库（重要！）
/opt/edu-server/backup-db.sh

# 2. 拉取/上传新版本Jar包
cd /opt/edu-server
cp edu-server-app-1.0.0-SNAPSHOT.jar edu-server-app-1.0.0-SNAPSHOT.jar.bak
# 上传新jar包到此目录

# 3. 停止服务
sudo systemctl stop edu-server

# 4. 如需执行数据库迁移脚本，在此步执行
# mysql -u edu_user -p edu_system < migration-script.sql

# 5. 启动服务
sudo systemctl start edu-server

# 6. 检查启动日志
sudo journalctl -u edu-server -f
# 等待出现 "Started EduApplication" 字样

# 7. 健康检查
curl http://localhost:8080/actuator/health

# 8. 业务验证（登录、核心功能测试）
```

### 6.2 回滚步骤

如更新后出现问题：

```bash
# 1. 停止服务
sudo systemctl stop edu-server

# 2. 回滚Jar包
cd /opt/edu-server
mv edu-server-app-1.0.0-SNAPSHOT.jar edu-server-app-1.0.0-SNAPSHOT.jar.failed
mv edu-server-app-1.0.0-SNAPSHOT.jar.bak edu-server-app-1.0.0-SNAPSHOT.jar

# 3. 如数据库有变更，回滚数据库
# 从备份恢复：
# gunzip < backup_file.sql.gz | mysql -u edu_user -p edu_system

# 4. 启动服务
sudo systemctl start edu-server

# 5. 验证服务正常
curl http://localhost:8080/actuator/health
```

---

## 七、常见问题排查

### 7.1 服务无法启动

**排查步骤：**
1. 查看日志：`journalctl -u edu-server -n 200 --no-pager`
2. 常见原因：
   - 数据库连接失败：检查MySQL是否启动、配置是否正确
   - Redis连接失败：检查Redis是否启动、密码是否正确
   - 端口被占用：`netstat -tlnp | grep 8080`
   - JWT密钥太短：检查JWT_SECRET长度
   - 磁盘满：`df -h`
   - 权限问题：检查/opt/edu-server目录权限

```bash
# 详细排查
# 1. 查看端口占用
netstat -tlnp | grep 8080

# 2. 手动启动查看详细错误
cd /opt/edu-server
java -jar edu-server-app-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod

# 3. 检查数据库连接
mysql -h $DB_HOST -u $DB_USERNAME -p$DB_PASSWORD -e "SELECT 1"
```

### 7.2 接口响应慢

**排查步骤：**
1. 查看服务器CPU/内存/IO是否正常
2. 查看MySQL慢查询日志：`tail -f /var/log/mysql/slow.log`
3. 查看是否有Full GC：
   ```bash
   # 查看GC统计
   jstat -gc $(pgrep -f edu-server-app) 1s 10
   ```
4. 检查Redis是否正常
5. 检查是否有大分页查询（pageSize过大）

### 7.3 内存溢出(OOM)

```bash
# 1. 查看是否有OOM日志
dmesg | grep -i "out of memory"
journalctl -u edu-server | grep -i "OutOfMemoryError"

# 2. 生成堆dump分析（需要配置JVM参数 -XX:+HeapDumpOnOutOfMemoryError）
# 在systemd配置中添加：
# Environment="JAVA_OPTS=-Xms512m -Xmx2g -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/edu-server/logs/"

# 3. 调整JVM堆内存参数
# 编辑 /etc/systemd/system/edu-server.service，修改-Xmx参数
sudo systemctl daemon-reload
sudo systemctl restart edu-server
```

### 7.4 数据库连接耗尽

```bash
# 1. 查看当前连接数
mysql -u root -p -e "SHOW STATUS LIKE 'Threads_connected';"
mysql -u root -p -e "SHOW PROCESSLIST;"

# 2. 查看连接池配置
# HikariCP配置检查maximum-pool-size是否合理

# 3. 检查是否有长事务/慢SQL阻塞
mysql -u root -p -e "SELECT * FROM information_schema.innodb_trx \G"
```

### 7.5 文件上传失败

1. 检查上传目录权限：`ls -la /opt/edu-server/upload`
2. 检查磁盘空间：`df -h`
3. 检查Nginx client_max_body_size配置
4. 检查文件大小是否超过20MB限制
5. 检查文件扩展名是否在白名单内

### 7.6 微信小程序无法连接

1. 检查服务器域名是否已配置
2. 确认HTTPS证书有效
3. 检查Nginx SSL配置是否兼容微信TLS要求
4. 后端服务是否正常运行
5. 检查防火墙/安全组是否开放443端口

---

## 八、紧急联系人

| 角色 | 姓名 | 电话 | 职责 |
|------|------|------|------|
| 运维负责人 | | | 服务器/环境问题 |
| 后端开发 | | | 接口/业务逻辑问题 |
| 前端开发 | | | 页面/小程序问题 |
| 产品负责人 | | | 业务问题确认 |

---

## 九、日常巡检清单（每日）

- [ ] 服务运行状态正常（systemctl status）
- [ ] 健康检查端点返回UP
- [ ] CPU/内存/磁盘使用率正常
- [ ] 错误日志无新增异常ERROR
- [ ] Nginx访问日志无大量5xx错误
- [ ] 数据库连接正常
- [ ] Redis连接正常
- [ ] 定时备份正常执行
- [ ] 备份文件正常生成

每周巡检：
- [ ] 检查慢查询日志，优化慢SQL
- [ ] 验证备份文件可用性
- [ ] 清理过期日志文件
- [ ] 检查安全更新

---

**文档结束 - 最后更新：2026-07-27**
