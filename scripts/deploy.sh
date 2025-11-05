#!/bin/bash
# 图书管理系统部署脚本
# 使用方法: ./deploy.sh [dev|prod]

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 环境配置
ENV=${1:-prod}
APP_NAME="library-management-system"
APP_VERSION="1.0.0"
BASE_DIR="/opt/library-management"
BACKEND_DIR="$BASE_DIR/backend"
FRONTEND_DIR="$BASE_DIR/frontend"
LOG_DIR="$BASE_DIR/logs"
BACKUP_DIR="$BASE_DIR/backup"

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查是否为root用户
check_root() {
    if [ "$EUID" -ne 0 ]; then 
        log_error "请使用root权限运行此脚本"
        exit 1
    fi
}

# 创建目录
create_directories() {
    log_info "创建部署目录..."
    mkdir -p $BASE_DIR
    mkdir -p $BACKEND_DIR
    mkdir -p $FRONTEND_DIR
    mkdir -p $LOG_DIR
    mkdir -p $BACKUP_DIR
    log_info "目录创建完成"
}

# 部署后端
deploy_backend() {
    log_info "开始部署后端..."
    
    # 检查jar包是否存在
    JAR_FILE="backend/target/${APP_NAME}-${APP_VERSION}.jar"
    if [ ! -f "$JAR_FILE" ]; then
        log_error "JAR文件不存在: $JAR_FILE"
        log_info "请先执行: cd backend && mvn clean package"
        exit 1
    fi
    
    # 备份旧版本
    if [ -f "$BACKEND_DIR/${APP_NAME}-${APP_VERSION}.jar" ]; then
        log_info "备份旧版本..."
        BACKUP_FILE="$BACKUP_DIR/backend_$(date +%Y%m%d_%H%M%S).jar"
        cp "$BACKEND_DIR/${APP_NAME}-${APP_VERSION}.jar" "$BACKUP_FILE"
        log_info "备份完成: $BACKUP_FILE"
    fi
    
    # 复制jar包
    log_info "复制JAR文件..."
    cp "$JAR_FILE" "$BACKEND_DIR/"
    
    # 复制配置文件
    if [ -f "backend/src/main/resources/application-${ENV}.yml" ]; then
        log_info "复制配置文件..."
        cp "backend/src/main/resources/application-${ENV}.yml" "$BACKEND_DIR/application-prod.yml"
    fi
    
    log_info "后端部署完成"
}

# 部署前端
deploy_frontend() {
    log_info "开始部署前端..."
    
    # 检查dist目录是否存在
    if [ ! -d "frontend/dist" ]; then
        log_error "前端构建文件不存在: frontend/dist"
        log_info "请先执行: cd frontend && npm install && npm run build"
        exit 1
    fi
    
    # 备份旧版本
    if [ -d "$FRONTEND_DIR/dist" ]; then
        log_info "备份旧版本..."
        BACKUP_FILE="$BACKUP_DIR/frontend_$(date +%Y%m%d_%H%M%S).tar.gz"
        tar -czf "$BACKUP_FILE" -C "$FRONTEND_DIR" dist
        log_info "备份完成: $BACKUP_FILE"
    fi
    
    # 复制前端文件
    log_info "复制前端文件..."
    rm -rf "$FRONTEND_DIR/dist"
    cp -r "frontend/dist" "$FRONTEND_DIR/"
    
    log_info "前端部署完成"
}

# 创建启动脚本
create_start_script() {
    log_info "创建启动脚本..."
    
    cat > "$BACKEND_DIR/start.sh" << 'EOF'
#!/bin/bash
cd /opt/library-management/backend
nohup java -jar \
  -Xms512m \
  -Xmx2048m \
  -XX:+UseG1GC \
  -Dspring.profiles.active=prod \
  library-management-system-1.0.0.jar \
  > /opt/library-management/logs/backend.log 2>&1 &
echo $! > /opt/library-management/backend/app.pid
echo "应用已启动，PID: $(cat /opt/library-management/backend/app.pid)"
EOF

    chmod +x "$BACKEND_DIR/start.sh"
    log_info "启动脚本创建完成"
}

# 创建停止脚本
create_stop_script() {
    log_info "创建停止脚本..."
    
    cat > "$BACKEND_DIR/stop.sh" << 'EOF'
#!/bin/bash
if [ -f /opt/library-management/backend/app.pid ]; then
    PID=$(cat /opt/library-management/backend/app.pid)
    if ps -p $PID > /dev/null; then
        kill $PID
        rm /opt/library-management/backend/app.pid
        echo "应用已停止"
    else
        echo "应用未运行"
        rm /opt/library-management/backend/app.pid
    fi
else
    echo "应用未运行"
fi
EOF

    chmod +x "$BACKEND_DIR/stop.sh"
    log_info "停止脚本创建完成"
}

# 创建重启脚本
create_restart_script() {
    log_info "创建重启脚本..."
    
    cat > "$BACKEND_DIR/restart.sh" << 'EOF'
#!/bin/bash
/opt/library-management/backend/stop.sh
sleep 2
/opt/library-management/backend/start.sh
EOF

    chmod +x "$BACKEND_DIR/restart.sh"
    log_info "重启脚本创建完成"
}

# 设置权限
set_permissions() {
    log_info "设置文件权限..."
    
    # 创建应用用户（如果不存在）
    if ! id "library" &>/dev/null; then
        useradd -m -s /bin/bash library
        log_info "创建应用用户: library"
    fi
    
    # 设置目录权限
    chown -R library:library $BASE_DIR
    chmod 755 $BASE_DIR
    chmod 755 $BACKEND_DIR
    chmod 755 $FRONTEND_DIR
    chmod 755 $LOG_DIR
    chmod 755 $BACKUP_DIR
    
    log_info "权限设置完成"
}

# 主函数
main() {
    log_info "=========================================="
    log_info "图书管理系统部署脚本"
    log_info "环境: $ENV"
    log_info "=========================================="
    
    check_root
    create_directories
    deploy_backend
    deploy_frontend
    create_start_script
    create_stop_script
    create_restart_script
    set_permissions
    
    log_info "=========================================="
    log_info "部署完成！"
    log_info "=========================================="
    log_info "启动应用: $BACKEND_DIR/start.sh"
    log_info "停止应用: $BACKEND_DIR/stop.sh"
    log_info "重启应用: $BACKEND_DIR/restart.sh"
    log_info "查看日志: tail -f $LOG_DIR/backend.log"
}

# 执行主函数
main

