#!/bin/bash

# 钱包API测试脚本
# 测试钱包相关接口功能

set -e

# 配置信息
BASE_URL="http://localhost:8080"
TOKEN="your_jwt_token_here"

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

# 检查服务是否运行
check_service() {
    log_info "检查服务是否运行..."
    
    if curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1; then
        log_info "服务运行正常"
    else
        log_error "服务未运行，请先启动服务"
        exit 1
    fi
}

# 测试获取钱包信息
test_get_wallet_info() {
    log_info "测试获取钱包信息..."
    
    response=$(curl -s -H "Authorization: Bearer $TOKEN" \
        "$BASE_URL/api/wallet")
    
    echo "响应: $response"
    
    if echo "$response" | grep -q '"code":0'; then
        log_info "获取钱包信息成功"
    else
        log_error "获取钱包信息失败"
    fi
}

# 测试充值
test_recharge() {
    log_info "测试充值功能..."
    
    response=$(curl -s -X POST \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{"amount": 100.00}' \
        "$BASE_URL/api/wallet/recharge")
    
    echo "响应: $response"
    
    if echo "$response" | grep -q '"code":0'; then
        log_info "充值成功"
    else
        log_error "充值失败"
    fi
}

# 测试提现申请
test_withdraw() {
    log_info "测试提现申请..."
    
    response=$(curl -s -X POST \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "amount": 50.00,
            "paymentMethod": "wechat",
            "paymentAccount": "13800138000",
            "remark": "测试提现"
        }' \
        "$BASE_URL/api/wallet/withdraw")
    
    echo "响应: $response"
    
    if echo "$response" | grep -q '"code":0'; then
        log_info "提现申请成功"
    else
        log_error "提现申请失败"
    fi
}

# 测试获取交易记录
test_get_transactions() {
    log_info "测试获取交易记录..."
    
    response=$(curl -s -H "Authorization: Bearer $TOKEN" \
        "$BASE_URL/api/wallet/transactions?page=1&pageSize=10")
    
    echo "响应: $response"
    
    if echo "$response" | grep -q '"code":0'; then
        log_info "获取交易记录成功"
    else
        log_error "获取交易记录失败"
    fi
}

# 测试获取提现记录
test_get_withdrawals() {
    log_info "测试获取提现记录..."
    
    response=$(curl -s -H "Authorization: Bearer $TOKEN" \
        "$BASE_URL/api/wallet/withdrawals?page=1&pageSize=10")
    
    echo "响应: $response"
    
    if echo "$response" | grep -q '"code":0'; then
        log_info "获取提现记录成功"
    else
        log_error "获取提现记录失败"
    fi
}

# 测试获取钱包配置
test_get_config() {
    log_info "测试获取钱包配置..."
    
    response=$(curl -s -H "Authorization: Bearer $TOKEN" \
        "$BASE_URL/api/wallet/config")
    
    echo "响应: $response"
    
    if echo "$response" | grep -q '"code":0'; then
        log_info "获取钱包配置成功"
    else
        log_error "获取钱包配置失败"
    fi
}

# 测试获取钱包统计
test_get_statistics() {
    log_info "测试获取钱包统计..."
    
    response=$(curl -s -H "Authorization: Bearer $TOKEN" \
        "$BASE_URL/api/wallet/statistics?period=month")
    
    echo "响应: $response"
    
    if echo "$response" | grep -q '"code":0'; then
        log_info "获取钱包统计成功"
    else
        log_error "获取钱包统计失败"
    fi
}

# 测试处理延迟到账
test_process_pending_withdrawals() {
    log_info "测试处理延迟到账..."
    
    response=$(curl -s -X POST \
        -H "Authorization: Bearer $TOKEN" \
        "$BASE_URL/api/wallet/process-pending-withdrawals")
    
    echo "响应: $response"
    
    if echo "$response" | grep -q '"code":0'; then
        log_info "处理延迟到账成功"
    else
        log_error "处理延迟到账失败"
    fi
}

# 显示使用说明
show_usage() {
    echo "钱包API测试脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -u, --url URL      API基础URL (默认: http://localhost:8080)"
    echo "  -t, --token TOKEN  JWT Token"
    echo "  --all              运行所有测试"
    echo "  --info             测试获取钱包信息"
    echo "  --recharge         测试充值"
    echo "  --withdraw         测试提现"
    echo "  --transactions     测试获取交易记录"
    echo "  --withdrawals      测试获取提现记录"
    echo "  --config           测试获取配置"
    echo "  --statistics       测试获取统计"
    echo "  --pending          测试处理延迟到账"
    echo "  --help             显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 --all"
    echo "  $0 --recharge --withdraw"
    echo "  $0 -t your_token_here --info"
}

# 解析命令行参数
RUN_ALL=false
RUN_INFO=false
RUN_RECHARGE=false
RUN_WITHDRAW=false
RUN_TRANSACTIONS=false
RUN_WITHDRAWALS=false
RUN_CONFIG=false
RUN_STATISTICS=false
RUN_PENDING=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -u|--url)
            BASE_URL="$2"
            shift 2
            ;;
        -t|--token)
            TOKEN="$2"
            shift 2
            ;;
        --all)
            RUN_ALL=true
            shift
            ;;
        --info)
            RUN_INFO=true
            shift
            ;;
        --recharge)
            RUN_RECHARGE=true
            shift
            ;;
        --withdraw)
            RUN_WITHDRAW=true
            shift
            ;;
        --transactions)
            RUN_TRANSACTIONS=true
            shift
            ;;
        --withdrawals)
            RUN_WITHDRAWALS=true
            shift
            ;;
        --config)
            RUN_CONFIG=true
            shift
            ;;
        --statistics)
            RUN_STATISTICS=true
            shift
            ;;
        --pending)
            RUN_PENDING=true
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

# 如果没有指定任何测试，默认运行所有测试
if [[ "$RUN_ALL" == false && "$RUN_INFO" == false && "$RUN_RECHARGE" == false && \
      "$RUN_WITHDRAW" == false && "$RUN_TRANSACTIONS" == false && "$RUN_WITHDRAWALS" == false && \
      "$RUN_CONFIG" == false && "$RUN_STATISTICS" == false && "$RUN_PENDING" == false ]]; then
    RUN_ALL=true
fi

# 主函数
main() {
    log_info "开始钱包API测试..."
    log_info "API地址: $BASE_URL"
    
    # 检查服务
    check_service
    
    # 检查Token
    if [[ "$TOKEN" == "your_jwt_token_here" ]]; then
        log_warn "请设置有效的JWT Token"
        log_warn "使用方法: $0 -t your_actual_token"
        exit 1
    fi
    
    # 运行测试
    if [[ "$RUN_ALL" == true || "$RUN_INFO" == true ]]; then
        test_get_wallet_info
        echo ""
    fi
    
    if [[ "$RUN_ALL" == true || "$RUN_RECHARGE" == true ]]; then
        test_recharge
        echo ""
    fi
    
    if [[ "$RUN_ALL" == true || "$RUN_WITHDRAW" == true ]]; then
        test_withdraw
        echo ""
    fi
    
    if [[ "$RUN_ALL" == true || "$RUN_TRANSACTIONS" == true ]]; then
        test_get_transactions
        echo ""
    fi
    
    if [[ "$RUN_ALL" == true || "$RUN_WITHDRAWALS" == true ]]; then
        test_get_withdrawals
        echo ""
    fi
    
    if [[ "$RUN_ALL" == true || "$RUN_CONFIG" == true ]]; then
        test_get_config
        echo ""
    fi
    
    if [[ "$RUN_ALL" == true || "$RUN_STATISTICS" == true ]]; then
        test_get_statistics
        echo ""
    fi
    
    if [[ "$RUN_ALL" == true || "$RUN_PENDING" == true ]]; then
        test_process_pending_withdrawals
        echo ""
    fi
    
    log_info "钱包API测试完成！"
}

# 执行主函数
main "$@"
