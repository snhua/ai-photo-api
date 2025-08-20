#!/bin/bash

# 私钥格式检查脚本

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== 微信支付私钥格式检查工具 ===${NC}"

# 检查私钥文件路径
PRIVATE_KEY_PATH="${1:-/path/to/your/private_key.pem}"

if [ ! -f "$PRIVATE_KEY_PATH" ]; then
    echo -e "${RED}错误：私钥文件不存在: $PRIVATE_KEY_PATH${NC}"
    echo -e "${YELLOW}请提供正确的私钥文件路径作为参数${NC}"
    echo -e "${YELLOW}用法: ./check_private_key.sh /path/to/your/private_key.pem${NC}"
    exit 1
fi

echo -e "${GREEN}检查私钥文件: $PRIVATE_KEY_PATH${NC}"

# 检查文件内容
echo -e "\n${YELLOW}1. 检查文件内容格式${NC}"
head -5 "$PRIVATE_KEY_PATH"
echo "..."

# 检查是否包含PEM头
if grep -q "-----BEGIN" "$PRIVATE_KEY_PATH"; then
    echo -e "${GREEN}✓ 文件包含PEM头${NC}"
    
    # 检查PEM头类型
    if grep -q "-----BEGIN PRIVATE KEY-----" "$PRIVATE_KEY_PATH"; then
        echo -e "${GREEN}✓ 检测到PKCS#8格式私钥${NC}"
        KEY_TYPE="PKCS8"
    elif grep -q "-----BEGIN RSA PRIVATE KEY-----" "$PRIVATE_KEY_PATH"; then
        echo -e "${GREEN}✓ 检测到RSA私钥格式${NC}"
        KEY_TYPE="RSA"
    else
        echo -e "${YELLOW}⚠ 未知的PEM格式${NC}"
        KEY_TYPE="UNKNOWN"
    fi
else
    echo -e "${RED}✗ 文件不包含PEM头${NC}"
    KEY_TYPE="NO_PEM"
fi

# 检查Base64编码
echo -e "\n${YELLOW}2. 检查Base64编码${NC}"
if [ "$KEY_TYPE" != "NO_PEM" ]; then
    # 提取Base64内容
    BASE64_CONTENT=$(sed -n '/-----BEGIN/,/-----END/p' "$PRIVATE_KEY_PATH" | grep -v "-----BEGIN" | grep -v "-----END" | tr -d '\n')
    
    # 检查Base64字符
    if echo "$BASE64_CONTENT" | grep -q "^[A-Za-z0-9+/]*=*$"; then
        echo -e "${GREEN}✓ Base64编码格式正确${NC}"
    else
        echo -e "${RED}✗ Base64编码格式错误${NC}"
        echo -e "${YELLOW}发现非法字符:${NC}"
        echo "$BASE64_CONTENT" | grep -o '[^A-Za-z0-9+/=]' | sort | uniq
    fi
fi

# 检查文件权限
echo -e "\n${YELLOW}3. 检查文件权限${NC}"
ls -la "$PRIVATE_KEY_PATH"

# 检查文件大小
echo -e "\n${YELLOW}4. 检查文件大小${NC}"
FILE_SIZE=$(stat -f%z "$PRIVATE_KEY_PATH" 2>/dev/null || stat -c%s "$PRIVATE_KEY_PATH" 2>/dev/null)
echo -e "文件大小: ${FILE_SIZE} 字节"

if [ "$FILE_SIZE" -lt 100 ]; then
    echo -e "${RED}⚠ 文件太小，可能不是有效的私钥文件${NC}"
elif [ "$FILE_SIZE" -gt 10000 ]; then
    echo -e "${YELLOW}⚠ 文件较大，请确认是否为私钥文件${NC}"
else
    echo -e "${GREEN}✓ 文件大小正常${NC}"
fi

# 提供修复建议
echo -e "\n${BLUE}=== 修复建议 ===${NC}"

if [ "$KEY_TYPE" = "NO_PEM" ]; then
    echo -e "${YELLOW}如果您的私钥文件没有PEM头，请按以下格式添加：${NC}"
    echo "-----BEGIN PRIVATE KEY-----"
    echo "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC..."
    echo "-----END PRIVATE KEY-----"
    
    echo -e "\n${YELLOW}或者如果是RSA私钥：${NC}"
    echo "-----BEGIN RSA PRIVATE KEY-----"
    echo "MIIEpAIBAAKCAQEA..."
    echo "-----END RSA PRIVATE KEY-----"
fi

if [ "$KEY_TYPE" = "RSA" ]; then
    echo -e "${YELLOW}检测到RSA私钥格式，建议转换为PKCS#8格式：${NC}"
    echo "openssl rsa -in your_rsa_key.pem -out your_pkcs8_key.pem"
fi

echo -e "\n${YELLOW}确保私钥文件：${NC}"
echo "1. 包含正确的PEM头尾"
echo "2. 使用正确的Base64编码"
echo "3. 没有多余的空格或换行符"
echo "4. 文件权限设置为600（仅所有者可读）"

echo -e "\n${GREEN}=== 检查完成 ===${NC}"
