#!/bin/bash

# 绘画师作品接口测试脚本

echo "=== 绘画师作品接口测试 ==="

# 基础URL
BASE_URL="http://localhost:8080"

echo ""
echo "1. 测试获取绘画师作品列表（分页）"
echo "预期：应该返回绘画师ID为2的作品列表"
curl -X GET "${BASE_URL}/api/artists/2/works?page=1&pageSize=5" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "2. 测试获取绘画师作品列表（按最新排序）"
echo "预期：应该返回按创建时间倒序排列的作品"
curl -X GET "${BASE_URL}/api/artists/2/works?page=1&pageSize=5&sort=latest" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "3. 测试获取绘画师作品列表（按价格排序）"
echo "预期：应该返回按价格倒序排列的作品"
curl -X GET "${BASE_URL}/api/artists/2/works?page=1&pageSize=5&sort=price" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "4. 测试获取绘画师作品列表（分类筛选）"
echo "预期：应该返回指定分类的作品"
curl -X GET "${BASE_URL}/api/artists/2/works?page=1&pageSize=5&category=风景画" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "5. 测试获取绘画师作品列表（不分页）"
echo "预期：应该返回指定数量的作品列表"
curl -X GET "${BASE_URL}/api/artists/2/works/list?limit=3" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "6. 测试获取不存在的绘画师作品"
echo "预期：应该返回错误信息"
curl -X GET "${BASE_URL}/api/artists/999/works?page=1&pageSize=5" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "=== 绘画师作品接口测试完成 ==="
echo ""
echo "测试说明："
echo "- 接口路径：/api/artists/{id}/works"
echo "- 支持分页、排序、分类筛选"
echo "- 只返回状态为正常(status=1)的作品"
echo "- 支持按最新时间、价格排序" 