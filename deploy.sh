#!/bin/bash

# MoodShare 快速部署脚本

echo "🚀 开始部署 MoodShare 项目..."

# 检查 Docker 和 Docker Compose 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

# 检查端口是否被占用
echo "🔍 检查端口占用情况..."
if netstat -tuln | grep -q ":8080 "; then
    echo "⚠️  端口 8080 已被占用，请确保端口可用"
fi

if netstat -tuln | grep -q ":3306 "; then
    echo "⚠️  端口 3306 已被占用，如果有其他 MySQL 服务在运行，请修改 docker-compose.yml 中的端口配置"
fi

# 创建必要的目录
echo "📁 创建上传目录..."
mkdir -p uploads

# 设置权限
chmod 755 uploads

# 构建并启动服务
echo "🏗️  构建镜像并启动服务..."
docker-compose up --build -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "🔍 检查服务状态..."
docker-compose ps

echo "✅ 部署完成！"
echo "🌐 应用访问地址: http://localhost:8080"
echo "📊 API 文档地址: http://localhost:8080/swagger-ui/index.html"
echo ""
echo "📝 其他有用命令："
echo "  查看日志: docker-compose logs -f"
echo "  停止服务: docker-compose down"
echo "  重启服务: docker-compose restart"
echo "  查看状态: docker-compose ps"
