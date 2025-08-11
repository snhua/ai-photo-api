#!/bin/bash

# 腾讯云COS头像上传测试脚本
BASE_URL="http://localhost:8080"

echo "=== 腾讯云COS头像上传功能测试 ==="

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

# 1. 获取头像上传授权
echo "1. 获取头像上传授权"
curl -X POST "${BASE_URL}/user/avatar/upload-policy" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Authorization: Bearer $TOKEN" \
  -d "fileName=avatar.jpg"

echo -e "\n\n"

# 2. 更新用户头像（模拟上传完成后的回调）
echo "2. 更新用户头像"
curl -X POST "${BASE_URL}/user/avatar/update" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Authorization: Bearer $TOKEN" \
  -d "objectKey=uploads/avatar/123/1640995200000.jpg"

echo -e "\n\n"

# 3. 获取用户信息（验证头像是否更新）
echo "3. 获取用户信息"
curl -X GET "${BASE_URL}/user/info" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n\n"

# 4. 测试不带token的请求（应该失败）
echo "4. 测试不带token的请求（应该返回401错误）"
curl -X POST "${BASE_URL}/user/avatar/upload-policy" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "fileName=avatar.jpg"

echo -e "\n\n"

echo "=== 测试完成 ==="
echo "注意："
echo "1. 确保已经通过登录接口获取了有效的JWT token"
echo "2. 将token替换到脚本中的TOKEN变量"
echo "3. 确保用户已经登录并且token没有过期"
echo "4. 实际使用时，前端需要先调用upload-policy接口获取授权，然后直接上传到腾讯云COS"
echo "5. 上传完成后调用update接口更新用户头像" 