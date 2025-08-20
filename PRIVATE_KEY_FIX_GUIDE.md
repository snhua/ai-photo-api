# 微信支付私钥格式修复指南

## 问题描述

错误信息：`Illegal base64 character 5f`

这个错误表明私钥文件的Base64编码格式不正确，可能是以下原因：

1. 私钥文件缺少PEM头尾
2. 私钥文件包含非法字符
3. 私钥文件格式不正确

## 解决方案

### 1. 检查私钥文件格式

使用提供的检查脚本：
```bash
./check_private_key.sh /path/to/your/private_key.pem
```

### 2. 标准PEM格式

私钥文件应该是以下格式之一：

#### PKCS#8格式（推荐）
```
-----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...
-----END PRIVATE KEY-----
```

#### RSA格式
```
-----BEGIN RSA PRIVATE KEY-----
MIIEpAIBAAKCAQEA...
-----END RSA PRIVATE KEY-----
```

### 3. 修复步骤

#### 步骤1：检查当前文件
```bash
cat /path/to/your/private_key.pem
```

#### 步骤2：如果缺少PEM头，添加正确的头尾
```bash
# 备份原文件
cp /path/to/your/private_key.pem /path/to/your/private_key.pem.backup

# 添加PEM头尾
echo "-----BEGIN PRIVATE KEY-----" > /path/to/your/private_key.pem.new
cat /path/to/your/private_key.pem.backup >> /path/to/your/private_key.pem.new
echo "-----END PRIVATE KEY-----" >> /path/to/your/private_key.pem.new

# 替换原文件
mv /path/to/your/private_key.pem.new /path/to/your/private_key.pem
```

#### 步骤3：设置正确的文件权限
```bash
chmod 600 /path/to/your/private_key.pem
```

### 4. 常见问题

#### 问题1：文件包含非法字符
- 检查是否有空格、换行符或其他字符
- 确保只有Base64字符（A-Z, a-z, 0-9, +, /, =）

#### 问题2：RSA格式需要转换
```bash
openssl rsa -in your_rsa_key.pem -out your_pkcs8_key.pem
```

#### 问题3：文件编码问题
```bash
# 转换为UTF-8编码
iconv -f GBK -t UTF-8 your_key.pem > your_key_utf8.pem
```

### 5. 验证修复

修复后重新运行检查脚本：
```bash
./check_private_key.sh /path/to/your/private_key.pem
```

### 6. 测试API

修复私钥后，重新测试提现功能：
```bash
./test_wechat_transfer_api.sh
```

## 注意事项

1. 私钥文件必须保密，不要提交到版本控制
2. 文件权限设置为600（仅所有者可读）
3. 确保文件路径在配置中正确设置
4. 定期备份私钥文件
