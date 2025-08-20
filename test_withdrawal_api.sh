#!/bin/bash

# 提现功能测试脚本
BASE_URL="http://localhost:8080/api"
TOKEN=""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== 提现功能测试 ===${NC}"

# 1. 用户登录获取token
echo -e "\n${YELLOW}1. 用户登录${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/user/login" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "test_code_123"
  }')

echo "登录响应: $LOGIN_RESPONSE"

# 提取token
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
if [ -z "$TOKEN" ]; then
    echo -e "${RED}登录失败，无法获取token${NC}"
    exit 1
fi

echo -e "${GREEN}获取到token: $TOKEN${NC}"

# 2. 获取钱包信息
echo -e "\n${YELLOW}2. 获取钱包信息${NC}"
WALLET_RESPONSE=$(curl -s -X GET "${BASE_URL}/wallet" \
  -H "Authorization: Bearer $TOKEN")

echo "钱包信息: $WALLET_RESPONSE"

# 3. 申请提现
echo -e "\n${YELLOW}3. 申请提现${NC}"
WITHDRAWAL_RESPONSE=$(curl -s -X POST "${BASE_URL}/wallet/withdraw" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10.00,
    "paymentMethod": "wechat",
    "paymentAccount": "test_openid_123",
    "remark": "测试提现"
  }')

echo "提现申请响应: $WITHDRAWAL_RESPONSE"

# 提取提现单号
WITHDRAWAL_NO=$(echo $WITHDRAWAL_RESPONSE | grep -o '"withdrawalNo":"[^"]*"' | cut -d'"' -f4)
if [ -z "$WITHDRAWAL_NO" ]; then
    echo -e "${RED}提现申请失败${NC}"
    exit 1
fi

echo -e "${GREEN}提现单号: $WITHDRAWAL_NO${NC}"

# 4. 查询提现状态
echo -e "\n${YELLOW}4. 查询提现状态${NC}"
STATUS_RESPONSE=$(curl -s -X GET "${BASE_URL}/wallet/withdrawal/${WITHDRAWAL_NO}/status" \
  -H "Authorization: Bearer $TOKEN")

echo "提现状态: $STATUS_RESPONSE"

# 5. 获取提现记录
echo -e "\n${YELLOW}5. 获取提现记录${NC}"
HISTORY_RESPONSE=$(curl -s -X GET "${BASE_URL}/wallet/withdrawal-history?page=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN")

echo "提现记录: $HISTORY_RESPONSE"

# 6. 获取交易记录
echo -e "\n${YELLOW}6. 获取交易记录${NC}"
TRANSACTION_RESPONSE=$(curl -s -X GET "${BASE_URL}/wallet/transactions?page=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN")

echo "交易记录: $TRANSACTION_RESPONSE"

echo -e "\n${GREEN}=== 提现功能测试完成 ===${NC}"
