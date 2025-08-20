# 微信商家转账 API 实现总结

## 概述

本次实现将原有的微信企业付款到零钱功能升级为新的微信商家转账 API，以符合微信支付最新的 API 规范。

## 主要变更

### 1. API 接口升级

#### 1.1 发起转账接口

- **原接口**：`POST /mmpaymkttransfers/promotion/transfers` (V2)
- **新接口**：`POST /v3/transfer/batches` (V3)

#### 1.2 查询转账接口

- **原接口**：`POST /mmpaymkttransfers/gettransferinfo` (V2)
- **新接口**：`GET /v3/transfer/batches/out-batch-no/{out_batch_no}` (V3)

### 2. 请求格式变更

#### 2.1 请求体格式

- **原格式**：XML 格式
- **新格式**：JSON 格式

#### 2.2 签名算法

- **原算法**：MD5 签名
- **新算法**：RSA-SHA256 签名

### 3. 响应格式变更

#### 3.1 响应体格式

- **原格式**：XML 格式
- **新格式**：JSON 格式

#### 3.2 状态字段

- **原状态**：`return_code`, `result_code`
- **新状态**：`batch_status`, `detail_status`

## 实现详情

### 1. 配置类更新

#### WechatPayConfig.java

```java
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
public class WechatPayConfig {
    // 新增字段
    private String serialNo;        // 商户证书序列号
    private String privateKeyPath;  // 商户私钥路径
}
```

### 2. 服务实现更新

#### WechatPayServiceImpl.java

##### 2.1 发起转账方法

```java
@Override
public Map<String, Object> transferToBalance(String partnerTradeNo, String openid,
    Integer amount, String description, String checkName, String reUserName) throws Exception {

    // 使用新的商家转账API
    String url = "https://api.mch.weixin.qq.com/v3/transfer/batches";

    // 构建JSON请求体
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("appid", wechatPayConfig.getAppId());
    requestBody.put("out_batch_no", partnerTradeNo);
    requestBody.put("batch_name", "提现到微信零钱");
    requestBody.put("batch_remark", description);

    // 转账明细
    Map<String, Object> transferDetail = new HashMap<>();
    transferDetail.put("out_detail_no", partnerTradeNo + "_detail");
    transferDetail.put("transfer_amount", amount);
    transferDetail.put("transfer_remark", description);
    transferDetail.put("openid", openid);

    // 如果金额超过2000元，必须进行实名校验
    if (amount > 200000) {
        transferDetail.put("user_name", reUserName);
    }

    List<Map<String, Object>> transferDetailList = new ArrayList<>();
    transferDetailList.add(transferDetail);
    requestBody.put("transfer_detail_list", transferDetailList);

    // 转账场景
    Map<String, Object> transferScene = new HashMap<>();
    transferScene.put("id", "COMMISSION"); // 佣金报酬场景
    requestBody.put("transfer_scene_id", transferScene);

    // 报备信息
    Map<String, Object> backgroundInfo = new HashMap<>();
    backgroundInfo.put("type", "岗位类型");
    backgroundInfo.put("content", "AI绘画师提现");
    requestBody.put("background_info", backgroundInfo);

    // 生成V3签名
    String signature = generateV3Signature(method, canonicalUrl, timestamp, nonceStr, body);

    // 发送请求并解析响应
    // ...
}
```

##### 2.2 查询转账方法

```java
@Override
public Map<String, Object> queryTransferToBalance(String partnerTradeNo) throws Exception {

    // 使用新的商家转账查询API
    String url = "https://api.mch.weixin.qq.com/v3/transfer/batches/out-batch-no/" + partnerTradeNo;

    // 生成V3签名
    String signature = generateV3Signature(method, canonicalUrl, timestamp, nonceStr, body);

    // 发送GET请求并解析响应
    // ...
}
```

##### 2.3 新增 V3 签名方法

```java
private String generateV3Signature(String method, String canonicalUrl, String timestamp,
    String nonceStr, String body) throws Exception {

    // 构建签名串
    String signatureString = method + "\n" +
                           canonicalUrl + "\n" +
                           timestamp + "\n" +
                           nonceStr + "\n" +
                           body + "\n";

    // 使用商户私钥签名
    PrivateKey privateKey = PemUtil.loadPrivateKey(wechatPayConfig.getPrivateKeyPath());
    Signature signature = Signature.getInstance("SHA256withRSA");
    signature.initSign(privateKey);
    signature.update(signatureString.getBytes("UTF-8"));
    byte[] signed = signature.sign();

    return Base64.getEncoder().encodeToString(signed);
}
```

##### 2.4 新增 HTTP 请求方法

```java
private String sendV3Request(String url, String body, Map<String, String> headers) throws Exception {
    CloseableHttpClient httpClient = HttpClients.createDefault();

    // 根据是否有请求体判断是GET还是POST请求
    if (body == null || body.isEmpty()) {
        // GET请求
        HttpGet httpGet = new HttpGet(url);
        // 设置请求头...
    } else {
        // POST请求
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头和请求体...
    }

    // 发送请求并返回响应
    // ...
}
```

### 3. 业务逻辑更新

#### WalletServiceImpl.java

##### 3.1 状态判断逻辑更新

```java
// 查询微信商家转账状态
Map<String, Object> queryResult = wechatPayService.queryTransferToBalance(withdrawalNo);
boolean success = (Boolean) queryResult.get("success");
if (success) {
    String detailStatus = (String) queryResult.get("detailStatus");
    if ("SUCCESS".equals(detailStatus)) {
        // 更新状态为成功
        withdrawalRequest.setStatus("success");
        withdrawalRequest.setRemark("微信商家转账成功");
    } else if ("FAILED".equals(detailStatus)) {
        // 更新状态为失败
        withdrawalRequest.setStatus("failed");
        withdrawalRequest.setRemark("微信商家转账失败：" + queryResult.get("failReason"));
    }
}
```

## 配置文件更新

### application.yml

```yaml
wechat:
  pay:
    appId: your_app_id
    mchId: your_mch_id
    apiV3Key: your_api_v3_key
    serialNo: your_serial_no # 新增
    privateKeyPath: /path/to/private_key.pem # 新增
```

## 测试验证

### 1. 测试脚本

创建了 `test_wechat_transfer_api.sh` 测试脚本，包含：

- 用户登录测试
- 获取钱包信息测试
- 申请提现测试
- 查询提现状态测试
- 获取提现记录测试
- 获取交易记录测试
- 错误情况测试

### 2. 测试用例

- 正常提现流程
- 金额验证（最小/最大金额）
- 余额不足情况
- 网络异常处理
- 签名错误处理

## 注意事项

### 1. 证书配置

- 需要配置商户私钥文件路径
- 需要配置证书序列号
- 确保证书文件有读取权限

### 2. 金额限制

- 单笔最大金额：2000 元
- 每日最大金额：10000 元
- 最小金额：0.3 元
- 超过 2000 元必须实名校验

### 3. 转账场景

- 使用"佣金报酬"场景
- 需要配置报备信息
- 支持实名校验

### 4. 错误处理

- 完善了错误信息处理
- 支持详细的错误码和错误消息
- 添加了异常日志记录

## 部署要求

### 1. 环境要求

- Java 8+
- Spring Boot 2.x
- 微信支付 SDK

### 2. 配置要求

- 微信商户号
- 商户 API 证书
- 商户私钥
- API V3 密钥

### 3. 网络要求

- 服务器 IP 需要在微信支付白名单中
- 确保网络连接稳定

## 总结

本次实现成功将微信支付接口从 V2 升级到 V3，主要改进包括：

1. **API 规范升级**：符合微信支付最新的 API V3 规范
2. **安全性提升**：使用 RSA-SHA256 签名算法
3. **功能完善**：支持更多转账场景和报备信息
4. **错误处理**：更详细的错误信息和状态处理
5. **测试覆盖**：完整的测试用例和测试脚本

实现后的系统能够正确调用微信商家转账 API，为用户提供安全、便捷的提现服务。
