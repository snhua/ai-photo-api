#!/bin/bash

# 横幅数据测试脚本

echo "=== 横幅数据测试 ==="

# 基础URL
BASE_URL="http://localhost:8080"

echo ""
echo "1. 测试获取首页横幅列表"
echo "预期：应该返回3个横幅，按sort_weight排序"
curl -X GET "${BASE_URL}/api/banners/home?limit=5" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "2. 测试获取分类页横幅"
echo "预期：应该返回1个category类型的横幅"
curl -X GET "${BASE_URL}/api/banners/type/category" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "3. 测试获取促销页横幅"
echo "预期：应该返回1个promotion类型的横幅"
curl -X GET "${BASE_URL}/api/banners/type/promotion" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "4. 测试获取所有有效横幅"
echo "预期：应该返回所有启用的横幅"
curl -X GET "${BASE_URL}/api/banners/active" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "5. 测试获取横幅列表（分页）"
echo "预期：应该返回分页的横幅列表"
curl -X GET "${BASE_URL}/api/banners?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo ""
echo "=== 横幅数据测试完成 ==="
echo ""
echo "测试说明："
echo "- 示例1：首页横幅 - AI绘画师招募 (sort_weight: 100)"
echo "- 示例2：分类页横幅 - 风景画专区 (sort_weight: 95)"
echo "- 示例3：促销页横幅 - 限时优惠活动 (sort_weight: 100)"
echo ""
echo "所有横幅都设置为启用状态(status=1)，有效期到2024年底" 