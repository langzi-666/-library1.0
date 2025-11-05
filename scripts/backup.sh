#!/bin/bash
# 图书管理系统备份脚本
# 使用方法: ./backup.sh [db|app|all]

set -e

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 配置
BACKUP_DIR="/opt/library-management/backup"
DATE=$(date +%Y%m%d_%H%M%S)
DB_USER="library_user"
DB_PASS="your_password"
DB_NAME="library1.4"
BACKUP_TYPE=${1:-all}

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# 创建备份目录
mkdir -p $BACKUP_DIR

# 数据库备份
backup_database() {
    log_info "开始备份数据库..."
    
    BACKUP_FILE="$BACKUP_DIR/library_db_$DATE.sql"
    
    # 备份数据库
    mysqldump -u $DB_USER -p$DB_PASS $DB_NAME > $BACKUP_FILE
    
    # 压缩备份文件
    gzip $BACKUP_FILE
    
    log_info "数据库备份完成: ${BACKUP_FILE}.gz"
    
    # 删除30天前的备份
    find $BACKUP_DIR -name "library_db_*.sql.gz" -mtime +30 -delete
    log_info "已清理30天前的数据库备份"
}

# 应用备份
backup_application() {
    log_info "开始备份应用..."
    
    BACKUP_FILE="$BACKUP_DIR/library_app_$DATE.tar.gz"
    
    # 备份应用和配置
    tar -czf $BACKUP_FILE \
        /opt/library-management/backend \
        /opt/library-management/frontend \
        --exclude='*.log' \
        --exclude='target/*' \
        --exclude='node_modules/*' \
        2>/dev/null || true
    
    log_info "应用备份完成: $BACKUP_FILE"
    
    # 删除30天前的备份
    find $BACKUP_DIR -name "library_app_*.tar.gz" -mtime +30 -delete
    log_info "已清理30天前的应用备份"
}

# 记录备份日志
log_backup() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" >> $BACKUP_DIR/backup.log
}

# 主函数
main() {
    log_info "=========================================="
    log_info "图书管理系统备份脚本"
    log_info "备份类型: $BACKUP_TYPE"
    log_info "=========================================="
    
    case $BACKUP_TYPE in
        db)
            backup_database
            log_backup "数据库备份完成"
            ;;
        app)
            backup_application
            log_backup "应用备份完成"
            ;;
        all)
            backup_database
            backup_application
            log_backup "完整备份完成"
            ;;
        *)
            log_warn "未知的备份类型: $BACKUP_TYPE"
            log_info "使用方法: ./backup.sh [db|app|all]"
            exit 1
            ;;
    esac
    
    log_info "=========================================="
    log_info "备份完成！"
    log_info "=========================================="
}

main

