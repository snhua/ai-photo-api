#!/bin/bash

# COS临时授权接口测试脚本
BASE_URL="http://localhost:8080"

echo "=== COS临时授权接口测试 ==="

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

# 1. 获取头像上传临时授权
echo "1. 获取头像上传临时授权"
curl -X GET "${BASE_URL}/cos/temp-auth?fileName=avatar.jpg&fileType=avatar" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n\n"

# 2. 获取作品图片上传临时授权
echo "2. 获取作品图片上传临时授权"
curl -X GET "${BASE_URL}/cos/temp-auth?fileName=artwork.jpg&fileType=artwork" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n\n"

# 3. 获取订单参考图片上传临时授权
echo "3. 获取订单参考图片上传临时授权"
curl -X GET "${BASE_URL}/cos/temp-auth?fileName=reference.jpg&fileType=reference" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n\n"

# 4. 获取文件访问URL
echo "4. 获取文件访问URL"
curl -X POST "${BASE_URL}/cos/file-url" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Authorization: Bearer $TOKEN" \
  -d "objectKey=uploads/avatar/123/1640995200000.jpg"

echo -e "\n\n"

# 5. 删除文件
echo "5. 删除文件"
curl -X DELETE "${BASE_URL}/cos/file" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Authorization: Bearer $TOKEN" \
  -d "objectKey=uploads/avatar/123/1640995200000.jpg"

echo -e "\n\n"

# 6. 测试不带token的请求（应该失败）
echo "6. 测试不带token的请求（应该返回401错误）"
curl -X GET "${BASE_URL}/cos/temp-auth?fileName=test.jpg&fileType=test"

echo -e "\n\n"

echo "=== 测试完成 ==="
echo "注意："
echo "1. 确保已经通过登录接口获取了有效的JWT token"
echo "2. 将token替换到脚本中的TOKEN变量"
echo "3. 确保用户已经登录并且token没有过期"
echo "4. 实际使用时，前端需要先调用temp-auth接口获取授权，然后直接上传到腾讯云COS" 