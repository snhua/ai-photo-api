#!/bin/bash

# 测试横幅API接口的脚本

echo "=== 测试横幅API接口 ==="

# 基础URL
BASE_URL="http://localhost:8080"

echo ""
echo "1. 测试获取首页横幅列表"
curl -X GET "${BASE_URL}/api/banners/home?limit=5" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "2. 测试获取分类横幅列表"
curl -X GET "${BASE_URL}/api/banners/type/category" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "3. 测试获取所有有效横幅"
curl -X GET "${BASE_URL}/api/banners/active" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "4. 测试获取横幅列表（分页）"
curl -X GET "${BASE_URL}/api/banners?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "5. 测试创建横幅"
curl -X POST "${BASE_URL}/api/banners" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "测试横幅",
    "description": "这是一个测试横幅",
    "imageUrl": "https://example.com/test-banner.jpg",
    "linkUrl": "https://example.com/test-link",
    "type": "home",
    "sortWeight": 100,
    "status": 1
  }' \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "6. 测试获取横幅详情"
curl -X GET "${BASE_URL}/api/banners/1" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "=== 测试完成 ===" 