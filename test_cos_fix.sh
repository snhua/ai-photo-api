#!/bin/bash

# COS临时授权修复测试脚本
# 测试修复后的COS临时授权功能

echo "=== COS临时授权修复测试 ==="
echo ""

# 设置基础URL
BASE_URL="http://localhost:8080"
API_URL="$BASE_URL/cos/temp-auth"

echo "1. 测试未认证访问（应该返回401）"
echo "请求: GET $API_URL"
echo "参数: fileName=test.jpg&fileType=avatar"
echo ""

curl -X GET "$API_URL?fileName=test.jpg&fileType=avatar" \
  -H "Content-Type: application/json" \
  -w "\nHTTP状态码: %{http_code}\n" \
  -s

echo ""
echo "----------------------------------------"
echo ""

echo "2. 测试认证访问（需要有效token）"
echo "请先获取有效token，然后运行以下命令："
echo ""
echo "curl -X GET \"$API_URL?fileName=test.jpg&fileType=avatar\" \\"
echo "  -H \"Authorization: Bearer YOUR_TOKEN_HERE\" \\"
echo "  -H \"Content-Type: application/json\""
echo ""

echo "3. 测试不同文件类型"
echo "avatar类型:"
echo "curl -X GET \"$API_URL?fileName=avatar.jpg&fileType=avatar\" -H \"Authorization: Bearer YOUR_TOKEN_HERE\""
echo ""
echo "document类型:"
echo "curl -X GET \"$API_URL?fileName=document.pdf&fileType=document\" -H \"Authorization: Bearer YOUR_TOKEN_HERE\""
echo ""

echo "4. 预期响应格式"
echo "{"
echo "  \"code\": 200,"
echo "  \"message\": \"success\","
echo "  \"data\": {"
echo "    \"sessionToken\": \"...\","
echo "    \"policy\": \"...\","
echo "    \"qSignAlgorithm\": \"sha1\","
echo "    \"qAk\": \"...\","
echo "    \"qKeyTime\": \"...\","
echo "    \"qSignKey\": \"...\","
echo "    \"qSignature\": \"...\","
echo "    \"objectKey\": \"...\","
echo "    \"bucket\": \"...\","
echo "    \"region\": \"...\","
echo "    \"expire\": ..."
echo "  }"
echo "}"
echo ""

echo "=== 测试完成 ==="
echo ""
echo "注意事项："
echo "1. 如果STS配置正确，将返回真实的临时密钥"
echo "2. 如果STS配置有问题，将返回模拟数据"
echo "3. 所有情况下都应该返回完整的授权信息"
echo "4. 用户必须已认证才能访问此接口" 