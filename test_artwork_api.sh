#!/bin/bash

# 测试热门作品API接口的脚本

echo "=== 测试热门作品API接口 ==="

# 基础URL
BASE_URL="http://localhost:8080"

echo ""
echo "1. 测试获取默认热门作品列表"
curl -X GET "${BASE_URL}/api/artworks/hot" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "2. 测试获取5个热门作品"
curl -X GET "${BASE_URL}/api/artworks/hot?limit=5" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "3. 测试获取风景画分类的热门作品"
curl -X GET "${BASE_URL}/api/artworks/hot?limit=3&category=风景画" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "4. 测试获取作品列表"
curl -X GET "${BASE_URL}/api/artworks?page=1&pageSize=5" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "5. 测试搜索作品"
curl -X GET "${BASE_URL}/api/artworks/search?keyword=森林" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "=== 测试完成 ===" 