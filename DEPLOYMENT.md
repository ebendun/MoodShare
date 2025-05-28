# MoodShare 部署指南

## 🚀 快速部署（推荐）

### 使用 Docker Compose 一键部署

1. **确保安装了 Docker 和 Docker Compose**
   ```bash
   # Linux/Mac
   ./deploy.sh
   
   # Windows
   deploy.bat
   ```

2. **手动部署步骤**
   ```bash
   # 1. 构建并启动所有服务
   docker-compose up --build -d
   
   # 2. 查看服务状态
   docker-compose ps
   
   # 3. 查看日志
   docker-compose logs -f
   ```

3. **访问应用**
   - 应用地址：http://localhost:8080
   - API 文档：http://localhost:8080/swagger-ui/index.html

## 🔧 其他部署方式

### 1. 传统部署方式

**前端构建**
```bash
cd frontend
npm install
npm run build
```

**后端部署**
```bash
# 将前端构建产物复制到 Spring Boot 静态资源目录
cp -r frontend/dist/* src/main/resources/static/

# 打包后端
./mvnw clean package -DskipTests

# 运行
java -jar target/MoodShare-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 2. 云服务器部署

**准备工作**
1. 购买云服务器（阿里云、腾讯云、华为云等）
2. 安装 Docker 和 Docker Compose
3. 配置防火墙开放端口：8080、3306、6379

**部署步骤**
```bash
# 1. 上传项目文件到服务器
scp -r MoodShare-master root@your_server_ip:/opt/

# 2. 登录服务器
ssh root@your_server_ip

# 3. 进入项目目录
cd /opt/MoodShare-master

# 4. 修改 docker-compose.yml 中的密码
nano docker-compose.yml

# 5. 运行部署脚本
chmod +x deploy.sh
./deploy.sh
```

### 3. 阿里云/腾讯云容器服务

可以使用云厂商的容器服务（如阿里云 ACK、腾讯云 TKE）进行部署：

1. 构建镜像并推送到容器镜像仓库
2. 使用 Kubernetes 部署配置
3. 配置负载均衡和域名

## 📝 配置说明

### 环境变量配置

在生产环境中，建议通过环境变量配置敏感信息：

```env
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/moodshare
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_secure_password
SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379
```

### 数据库初始化

首次部署时，应用会自动创建数据库表结构。如需导入初始数据，可以：

1. 通过应用的 API 接口
2. 直接连接数据库执行 SQL
3. 使用数据迁移工具

## 🔒 安全建议

1. **修改默认密码**：更改 MySQL root 密码
2. **使用 HTTPS**：配置 SSL 证书
3. **防火墙配置**：只开放必要端口
4. **定期备份**：备份数据库和上传文件
5. **日志监控**：配置日志收集和监控

## 🛠️ 常用操作

```bash
# 查看日志
docker-compose logs -f app

# 重启应用
docker-compose restart app

# 停止所有服务
docker-compose down

# 备份数据库
docker exec moodshare-mysql mysqldump -u root -p moodshare > backup.sql

# 恢复数据库
docker exec -i moodshare-mysql mysql -u root -p moodshare < backup.sql
```

## 🐛 故障排除

1. **端口占用**：修改 docker-compose.yml 中的端口映射
2. **内存不足**：增加服务器内存或优化 JVM 参数
3. **数据库连接失败**：检查网络和密码配置
4. **文件上传失败**：检查 uploads 目录权限
