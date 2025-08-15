#!/bin/bash

# 钱包功能数据库迁移脚本
# 执行前请确保已备份数据库

set -e

# 配置信息
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="aiphone"
DB_USER="root"
DB_PASS=""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

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

# 检查MySQL连接
check_mysql_connection() {
    log_info "检查MySQL连接..."
    
    if [ -z "$DB_PASS" ]; then
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -e "SELECT 1;" > /dev/null 2>&1
    else
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1;" > /dev/null 2>&1
    fi
    
    if [ $? -eq 0 ]; then
        log_info "MySQL连接成功"
    else
        log_error "MySQL连接失败，请检查配置"
        exit 1
    fi
}

# 备份数据库
backup_database() {
    log_info "开始备份数据库..."
    
    BACKUP_FILE="aiphone_backup_$(date +%Y%m%d_%H%M%S).sql"
    
    if [ -z "$DB_PASS" ]; then
        mysqldump -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" "$DB_NAME" > "$BACKUP_FILE"
    else
        mysqldump -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" > "$BACKUP_FILE"
    fi
    
    if [ $? -eq 0 ]; then
        log_info "数据库备份完成: $BACKUP_FILE"
    else
        log_error "数据库备份失败"
        exit 1
    fi
}

# 执行迁移SQL
run_migration() {
    log_info "开始执行钱包功能数据库迁移..."
    
    MIGRATION_FILE="wallet_database_migration.sql"
    
    if [ ! -f "$MIGRATION_FILE" ]; then
        log_error "迁移文件不存在: $MIGRATION_FILE"
        exit 1
    fi
    
    if [ -z "$DB_PASS" ]; then
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" "$DB_NAME" < "$MIGRATION_FILE"
    else
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < "$MIGRATION_FILE"
    fi
    
    if [ $? -eq 0 ]; then
        log_info "数据库迁移执行成功"
    else
        log_error "数据库迁移执行失败"
        exit 1
    fi
}

# 验证迁移结果
verify_migration() {
    log_info "验证迁移结果..."
    
    # 检查新增字段
    log_info "检查用户表新增字段..."
    if [ -z "$DB_PASS" ]; then
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" "$DB_NAME" -e "
        SELECT COLUMN_NAME, DATA_TYPE, COLUMN_DEFAULT, COLUMN_COMMENT 
        FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_SCHEMA = '$DB_NAME' 
        AND TABLE_NAME = 'users' 
        AND COLUMN_NAME IN ('withdrawable_balance', 'total_income', 'total_withdraw', 'service_fee_total');"
    else
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
        SELECT COLUMN_NAME, DATA_TYPE, COLUMN_DEFAULT, COLUMN_COMMENT 
        FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_SCHEMA = '$DB_NAME' 
        AND TABLE_NAME = 'users' 
        AND COLUMN_NAME IN ('withdrawable_balance', 'total_income', 'total_withdraw', 'service_fee_total');"
    fi
    
    # 检查新增表
    log_info "检查新增表..."
    if [ -z "$DB_PASS" ]; then
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" "$DB_NAME" -e "
        SHOW TABLES LIKE '%wallet%';"
    else
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
        SHOW TABLES LIKE '%wallet%';"
    fi
    
    # 检查系统配置
    log_info "检查钱包系统配置..."
    if [ -z "$DB_PASS" ]; then
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" "$DB_NAME" -e "
        SELECT config_key, config_value, description 
        FROM system_configs 
        WHERE config_key LIKE 'wallet.%';"
    else
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
        SELECT config_key, config_value, description 
        FROM system_configs 
        WHERE config_key LIKE 'wallet.%';"
    fi
    
    log_info "迁移验证完成"
}

# 显示使用说明
show_usage() {
    echo "钱包功能数据库迁移脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --host HOST     数据库主机地址 (默认: localhost)"
    echo "  -P, --port PORT     数据库端口 (默认: 3306)"
    echo "  -u, --user USER     数据库用户名 (默认: root)"
    echo "  -p, --password PASS 数据库密码"
    echo "  -d, --database DB   数据库名称 (默认: aiphone)"
    echo "  --no-backup         跳过数据库备份"
    echo "  --help              显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 -h localhost -u root -p password -d aiphone"
    echo "  $0 --no-backup"
}

# 解析命令行参数
SKIP_BACKUP=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--host)
            DB_HOST="$2"
            shift 2
            ;;
        -P|--port)
            DB_PORT="$2"
            shift 2
            ;;
        -u|--user)
            DB_USER="$2"
            shift 2
            ;;
        -p|--password)
            DB_PASS="$2"
            shift 2
            ;;
        -d|--database)
            DB_NAME="$2"
            shift 2
            ;;
        --no-backup)
            SKIP_BACKUP=true
            shift
            ;;
        --help)
            show_usage
            exit 0
            ;;
        *)
            log_error "未知参数: $1"
            show_usage
            exit 1
            ;;
    esac
done

# 主函数
main() {
    log_info "开始钱包功能数据库迁移..."
    log_info "数据库配置: $DB_HOST:$DB_PORT/$DB_NAME (用户: $DB_USER)"
    
    # 检查MySQL连接
    check_mysql_connection
    
    # 备份数据库（除非跳过）
    if [ "$SKIP_BACKUP" = false ]; then
        backup_database
    else
        log_warn "跳过数据库备份"
    fi
    
    # 执行迁移
    run_migration
    
    # 验证迁移结果
    verify_migration
    
    log_info "钱包功能数据库迁移完成！"
    log_info "请检查迁移结果，确保所有表和数据都正确创建。"
    log_info "接下来需要更新后端代码以支持新的钱包功能。"
}

# 执行主函数
main "$@"
