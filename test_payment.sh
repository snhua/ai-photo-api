#!/bin/bash

# 微信支付功能测试脚本

echo "=== 微信支付功能测试 ==="
echo ""

# 设置基础URL和token
BASE_URL="http://localhost:8080"
TOKEN="your-jwt-token-here"

echo "1. 创建支付订单"
echo "请求: POST $BASE_URL/payment/create"
echo ""

curl -X POST "$BASE_URL/payment/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "orderNo": "ORDER202312010001",
    "amount": 99.99,
    "paymentMethod": "WECHAT",
    "description": "AI绘画服务费用",
    "clientIp": "127.0.0.1",
    "remark": "测试支付"
  }' \
  -w "\nHTTP状态码: %{http_code}\n" \
  -s

echo ""
echo "----------------------------------------"
echo ""

echo "2. 查询支付订单"
echo "请使用上面返回的paymentNo替换下面的PAYMENT_NO"
echo "请求: GET $BASE_URL/payment/PAYMENT_NO"
echo ""

echo "3. 查询支付状态"
echo "请求: GET $BASE_URL/payment/PAYMENT_NO/status"
echo ""

echo "4. 查询用户支付记录"
echo "请求: GET $BASE_URL/payment/user?current=1&size=10"
echo ""

curl -X GET "$BASE_URL/payment/user?current=1&size=10" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nHTTP状态码: %{http_code}\n" \
  -s

echo ""
echo "----------------------------------------"
echo ""

echo "5. 取消支付订单"
echo "请求: POST $BASE_URL/payment/PAYMENT_NO/cancel"
echo ""

echo "6. 申请退款"
echo "请求: POST $BASE_URL/payment/PAYMENT_NO/refund?amount=9999&reason=用户申请退款"
echo ""

echo "=== 测试完成 ==="
echo ""
echo "注意事项："
echo "1. 请先获取有效的JWT token"
echo "2. 请替换脚本中的TOKEN变量"
echo "3. 请使用实际的paymentNo替换PAYMENT_NO"
echo "4. 微信支付回调地址: $BASE_URL/payment/wechat/notify"
echo "5. 需要在微信商户平台配置回调地址"
echo "6. 新的支付状态: pending(待支付), success(支付成功), failed(支付失败), refunded(已退款)"
echo "7. 新的支付方式: wechat(微信支付), balance(余额支付)"
echo ""
echo "预期响应格式："
echo "{"
echo "  \"code\": 200,"
echo "  \"message\": \"success\","
echo "  \"data\": {"
echo "    \"paymentNo\": \"PAY202312010001\","
echo "    \"orderNo\": \"ORDER202312010001\","
echo "    \"amount\": 99.99,"
echo "    \"paymentMethod\": \"WECHAT\","
echo "    \"status\": \"PENDING\","
echo "    \"wechatPayParams\": {"
echo "      \"appId\": \"wx8888888888888888\","
echo "      \"timeStamp\": \"1414561699\","
echo "      \"nonceStr\": \"5K8264ILTKCH16CQ2502SI8ZNMTM67VS\","
echo "      \"prepayId\": \"prepay_id=wx201410272009395522657a690389285100\","
echo "      \"signType\": \"RSA\","
echo "      \"paySign\": \"oR9d8PuhnIc+YZ8cBHFCwfgpaK9gd7vaRvkYD7rthRAZ...\""
echo "    }"
echo "  }"
echo "}" 