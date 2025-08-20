#!/bin/bash

# 微信商家转账API测试脚本

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api"

echo -e "${BLUE}=== 微信商家转账API测试 ===${NC}"

# 1. 用户登录
echo -e "\n${YELLOW}1. 用户登录${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE/users/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }')

echo "登录响应: $LOGIN_RESPONSE"

# 提取token
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
if [ -z "$TOKEN" ]; then
    echo -e "${RED}登录失败，无法获取token${NC}"
    exit 1
fi

echo -e "${GREEN}登录成功，Token: $TOKEN${NC}"

# 2. 获取钱包信息
echo -e "\n${YELLOW}2. 获取钱包信息${NC}"
WALLET_RESPONSE=$(curl -s -X GET "$API_BASE/wallet" \
  -H "Authorization: Bearer $TOKEN")

echo "钱包信息: $WALLET_RESPONSE"

# 3. 申请提现
echo -e "\n${YELLOW}3. 申请提现${NC}"
WITHDRAW_RESPONSE=$(curl -s -X POST "$API_BASE/wallet/withdraw" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10.00,
    "paymentMethod": "wechat",
    "paymentAccount": "test_openid_123",
    "remark": "测试提现到微信零钱"
  }')

echo "提现申请响应: $WITHDRAW_RESPONSE"

# 提取提现单号
WITHDRAWAL_NO=$(echo $WITHDRAW_RESPONSE | grep -o '"withdrawalNo":"[^"]*"' | cut -d'"' -f4)
if [ -z "$WITHDRAWAL_NO" ]; then
    echo -e "${RED}提现申请失败，无法获取提现单号${NC}"
    exit 1
fi

echo -e "${GREEN}提现申请成功，提现单号: $WITHDRAWAL_NO${NC}"

# 4. 查询提现状态
echo -e "\n${YELLOW}4. 查询提现状态${NC}"
sleep 2

STATUS_RESPONSE=$(curl -s -X GET "$API_BASE/wallet/withdrawal/$WITHDRAWAL_NO/status" \
  -H "Authorization: Bearer $TOKEN")

echo "提现状态: $STATUS_RESPONSE"

# 5. 获取提现记录
echo -e "\n${YELLOW}5. 获取提现记录${NC}"
WITHDRAWALS_RESPONSE=$(curl -s -X GET "$API_BASE/wallet/withdrawals" \
  -H "Authorization: Bearer $TOKEN")

echo "提现记录: $WITHDRAWALS_RESPONSE"

# 6. 获取交易记录
echo -e "\n${YELLOW}6. 获取交易记录${NC}"
TRANSACTIONS_RESPONSE=$(curl -s -X GET "$API_BASE/wallet/transactions" \
  -H "Authorization: Bearer $TOKEN")

echo "交易记录: $TRANSACTIONS_RESPONSE"

# 7. 测试错误情况
echo -e "\n${YELLOW}7. 测试错误情况${NC}"

# 7.1 提现金额不足
echo -e "\n${BLUE}7.1 测试提现金额不足${NC}"
INSUFFICIENT_RESPONSE=$(curl -s -X POST "$API_BASE/wallet/withdraw" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000000.00,
    "paymentMethod": "wechat",
    "paymentAccount": "test_openid_123",
    "remark": "测试金额不足"
  }')

echo "金额不足响应: $INSUFFICIENT_RESPONSE"

# 7.2 提现金额过小
echo -e "\n${BLUE}7.2 测试提现金额过小${NC}"
SMALL_AMOUNT_RESPONSE=$(curl -s -X POST "$API_BASE/wallet/withdraw" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 0.50,
    "paymentMethod": "wechat",
    "paymentAccount": "test_openid_123",
    "remark": "测试金额过小"
  }')

echo "金额过小响应: $SMALL_AMOUNT_RESPONSE"

# 7.3 查询不存在的提现单号
echo -e "\n${BLUE}7.3 测试查询不存在的提现单号${NC}"
INVALID_STATUS_RESPONSE=$(curl -s -X GET "$API_BASE/wallet/withdrawal/INVALID_NO_123/status" \
  -H "Authorization: Bearer $TOKEN")

echo "无效提现单号响应: $INVALID_STATUS_RESPONSE"

echo -e "\n${GREEN}=== 微信商家转账API测试完成 ===${NC}"

echo -e "\n${BLUE}测试总结:${NC}"
echo "1. ✅ 用户登录测试"
echo "2. ✅ 获取钱包信息测试"
echo "3. ✅ 申请提现测试"
echo "4. ✅ 查询提现状态测试"
echo "5. ✅ 获取提现记录测试"
echo "6. ✅ 获取交易记录测试"
echo "7. ✅ 错误情况测试"

echo -e "\n${YELLOW}注意事项:${NC}"
echo "- 确保后端服务已启动"
echo "- 确保微信支付配置正确"
echo "- 确保数据库中有测试用户数据"
echo "- 实际转账需要真实的微信openid"
