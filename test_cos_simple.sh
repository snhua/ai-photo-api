#!/bin/bash

# 简化的COS临时授权测试脚本
BASE_URL="http://localhost:8080"

echo "=== 简化的COS临时授权测试 ==="

# 注意：这个测试需要有效的JWT token
TOKEN="your_jwt_token_here"

if [ "$TOKEN" = "your_jwt_token_here" ]; then
    echo "请先获取有效的JWT token，然后更新脚本中的TOKEN变量"
    echo "可以通过以下方式获取token："
    echo "1. 调用登录接口获取token"
    echo "2. 将token替换到脚本中的TOKEN变量"
    exit 1
fi

echo "使用token: $TOKEN"

# 1. 测试获取临时授权（应该返回401，因为token无效）
echo "1. 测试获取临时授权"
curl -X GET "${BASE_URL}/cos/temp-auth?fileName=avatar.jpg&fileType=avatar" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n\n"

# 2. 测试不带token的请求（应该返回401错误）
echo "2. 测试不带token的请求（应该返回401错误）"
curl -X GET "${BASE_URL}/cos/temp-auth?fileName=test.jpg&fileType=test"

echo -e "\n\n"

echo "=== 测试完成 ==="
echo "注意："
echo "1. 确保已经通过登录接口获取了有效的JWT token"
echo "2. 将token替换到脚本中的TOKEN变量"
echo "3. 确保用户已经登录并且token没有过期"
echo "4. 如果返回401错误，说明认证正常，需要有效的token才能获取临时授权" 