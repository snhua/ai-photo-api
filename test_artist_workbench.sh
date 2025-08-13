#!/bin/bash

# 画师工作台功能测试脚本
# 需要先登录获取token

BASE_URL="http://localhost:8080"
TOKEN=""

echo "=== 画师工作台功能测试 ==="

# 登录获取token（需要替换为实际的用户凭据）
echo "1. 登录获取token..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/user/login" \
  -H "Content-Type: application/json" \
  -d '{
    "openid": "test_artist_openid",
    "nickname": "测试画师",
    "avatar": "https://example.com/avatar.jpg"
  }')

echo "登录响应: $LOGIN_RESPONSE"

# 提取token（根据实际响应格式调整）
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
if [ -z "$TOKEN" ]; then
    echo "获取token失败，使用默认token进行测试"
    TOKEN="eyJhbGciOiJIUzI1NiJ9.test_token"
fi

echo "使用token: $TOKEN"

# 2. 获取可接订单列表
echo ""
echo "2. 获取可接订单列表..."
curl -s -X GET "$BASE_URL/artist/workbench/available-orders?page=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.'

# 3. 获取我的订单列表
echo ""
echo "3. 获取我的订单列表..."
curl -s -X GET "$BASE_URL/artist/workbench/my-orders?page=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.'

# 4. 接单（需要替换为实际的订单ID）
echo ""
echo "4. 接单..."
ORDER_ID=1
curl -s -X POST "$BASE_URL/artist/workbench/orders/$ORDER_ID/accept" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.'

# 5. 开始制作订单
echo ""
echo "5. 开始制作订单..."
curl -s -X POST "$BASE_URL/artist/workbench/orders/$ORDER_ID/start" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.'

# 6. 上传作品草稿
echo ""
echo "6. 上传作品草稿..."
# 创建一个测试图片文件
echo "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==" | base64 -d > test_draft.png

curl -s -X POST "$BASE_URL/artist/workbench/orders/$ORDER_ID/draft" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@test_draft.png" \
  -F "description=这是草稿说明" | jq '.'

# 7. 提交最终作品
echo ""
echo "7. 提交最终作品..."
curl -s -X POST "$BASE_URL/artist/workbench/orders/$ORDER_ID/deliver" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "artworkUrls": ["https://example.com/artwork1.jpg", "https://example.com/artwork2.jpg"],
    "title": "最终作品",
    "description": "这是最终完成的作品",
    "notes": "作品说明",
    "includeSourceFiles": true,
    "sourceFileUrls": ["https://example.com/source.psd"],
    "tags": ["插画", "人物"],
    "category": "插画",
    "style": "写实",
    "dimensions": "1920x1080",
    "format": "PNG",
    "resolution": "300DPI",
    "workHours": 8,
    "technicalNotes": "使用Photoshop绘制",
    "feedbackHandling": "已根据客户反馈进行调整"
  }' | jq '.'

# 8. 获取订单详情
echo ""
echo "8. 获取订单详情..."
curl -s -X GET "$BASE_URL/artist/workbench/orders/$ORDER_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.'

# 9. 获取我的作品列表
echo ""
echo "9. 获取我的作品列表..."
curl -s -X GET "$BASE_URL/artist/workbench/my-works?page=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.'

# 10. 上传作品到作品集
echo ""
echo "10. 上传作品到作品集..."
curl -s -X POST "$BASE_URL/artist/workbench/works" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@test_draft.png" \
  -F 'artworkDTO={
    "title": "我的作品",
    "description": "这是我在作品集中的作品",
    "category": "插画",
    "style": "写实",
    "price": 100.00,
    "tags": ["插画", "人物"]
  }' | jq '.'

# 11. 获取工作统计信息
echo ""
echo "11. 获取工作统计信息..."
curl -s -X GET "$BASE_URL/artist/workbench/statistics" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.'

# 清理测试文件
rm -f test_draft.png

echo ""
echo "=== 测试完成 ==="
echo "注意："
echo "1. 需要先确保数据库中有相应的订单数据"
echo "2. 需要确保COS服务配置正确"
echo "3. 需要确保用户已注册为画师角色"
echo "4. 部分功能可能需要根据实际业务逻辑调整" 