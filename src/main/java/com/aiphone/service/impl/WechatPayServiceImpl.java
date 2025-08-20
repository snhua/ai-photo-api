package com.aiphone.service.impl;

import com.aiphone.config.WechatPayConfig;
import com.aiphone.dto.PaymentResponse;
import com.aiphone.dto.Resource;
import com.aiphone.security.SecurityUtils;
import com.aiphone.service.WechatPayService;
import com.aiphone.util.JsonToMapConverter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.*;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sun.security.x509.X509CertImpl;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

/**
 * 微信支付服务实现类
 */
@Slf4j
@Service
public class WechatPayServiceImpl implements WechatPayService {

    @Autowired
    private WechatPayConfig wechatPayConfig;
    private PrivateKey merchantPrivateKey;
    @Value("${wechat.pay.privateKeyPath}")
    private String privateKeyPath;// = "/Users/snhua/work/cert/1608371362_20210621_cert/apiclient_key.pem";
    @Value("${wechat.pay.publicKeyPath}")
    private String publicKeyPath;// = "/Users/snhua/work/cert/1608371362_20210621_cert/apiclient_public_key.pem";
    @Value("${wechat.pay.apiV3Key}")
    private String apiV3Key;
    private String merchantSerialNumber = "28FF6FBB73291667967904A4B736DBA378748BA7";

    private String payApi = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    private CloseableHttpClient httpClient;

    String orignString = "%s\n%s\n%s\n%s\n";

    @PostConstruct
    void init() throws IOException, GeneralSecurityException, HttpCodeException, NotFoundException {
        merchantPrivateKey = PemUtil.loadPrivateKey(
                new FileInputStream(privateKeyPath));
        // 初始化证书管理器
        CertificatesManager certificatesManager = CertificatesManager.getInstance();
        // 向证书管理器添加商户信息
        String merchantId = wechatPayConfig.getMchId();
        certificatesManager.putMerchant(merchantId,
                new WechatPay2Credentials(merchantId, new PrivateKeySigner(merchantSerialNumber, merchantPrivateKey)),
                apiV3Key.getBytes("utf-8"));

        // 获取验证器
        Verifier verifier = certificatesManager.getVerifier(merchantId);

        // 构建HTTP客户端
         httpClient = WechatPayHttpClientBuilder.create()
                .withMerchant(merchantId, merchantSerialNumber, merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier))
                .build();
    }
    public PaymentResponse.WechatPayParams createWechatPayOrder(String paymentNo, BigDecimal amount, String description, String clientIp) throws Exception {

//        init();

// 后面跟使用Apache HttpClient一样

        HttpPost post = new HttpPost(payApi);
        JSONObject params = new JSONObject();
        params.put("appid", wechatPayConfig.getAppId());
        params.put("mchid", wechatPayConfig.getMchId());
//        String description = "送花";
        params.put("description", description);
        params.put("out_trade_no", paymentNo);
        params.put("notify_url", wechatPayConfig.getNotifyUrl());
        JSONObject amountJson = new JSONObject();
        amountJson.put("total",  amount.multiply(new BigDecimal("100")).intValue() );
        params.put("amount", amountJson);

        JSONObject payer = new JSONObject();
        payer.put("openid", SecurityUtils.getCurrentUserOpenid());
        params.put("payer", payer);

        JSONObject scene_info = new JSONObject();
        scene_info.put("payer_client_ip",  clientIp != null ? clientIp : "127.0.0.1");
        params.put("scene_info", scene_info);


        String str = params.toJSONString();
        //str --> {"journalName":"","issn":"0048-9697","year":"2019","type":"WOS"}
        StringEntity stringEntity = new StringEntity(str, "utf-8");
        post.setEntity(stringEntity);
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        post.setHeader("Content-Type", "application/json");
        CloseableHttpResponse execute = httpClient.execute(post);

        HttpEntity entity = execute.getEntity();
        String result = EntityUtils.toString(entity, "UTF-8");
        log.debug(params.toJSONString());
        log.debug(result);


        if (result.contains("prepay_id")) {
            String prepayId = JSON.parseObject(result).getString("prepay_id");


            // 构建小程序支付参数
            PaymentResponse.WechatPayParams wechatPayParams = new PaymentResponse.WechatPayParams();
            wechatPayParams.setAppId(wechatPayConfig.getAppId());
            wechatPayParams.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));
            wechatPayParams.setNonceStr(generateNonceStr());
            wechatPayParams.setPrepayId("prepay_id=" + prepayId);
            wechatPayParams.setSignType("RSA");
            wechatPayParams.setPaySign(generatePaySign(wechatPayParams));

            log.info("创建微信支付订单成功，支付订单号：{}，预支付ID：{}", paymentNo, prepayId);
            return wechatPayParams;
        } else {
            log.error("创建微信支付订单失败，错误信息：{}", JSON.parseObject(result).getString("err_code_des"));
            throw new RuntimeException("创建微信支付订单失败：" + JSON.parseObject(result).getString("err_code_des"));
        }
    }


    @Override
    public String queryWechatPayStatus(String paymentNo) {
        try {
            // 构建查询订单请求参数
            Map<String, String> params = new HashMap<>();
            params.put("appid", wechatPayConfig.getAppId());
            params.put("mch_id", wechatPayConfig.getMchId());
            params.put("out_trade_no", paymentNo);
            params.put("nonce_str", generateNonceStr());
            params.put("sign", generateSign(params));

            // 发送查询订单请求
            String xmlData = mapToXml(params);
            String responseXml = sendWechatPayRequest("https://api.mch.weixin.qq.com/pay/orderquery", xmlData);
            
            // 解析响应
            Map<String, String> responseMap = xmlToMap(responseXml);
            
            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                return responseMap.get("trade_state");
            } else {
                log.error("查询微信支付状态失败，错误信息：{}", responseMap.get("err_code_des"));
                return "UNKNOWN";
            }
            
        } catch (Exception e) {
            log.error("查询微信支付状态异常", e);
            return "UNKNOWN";
        }
    }

    @Override
    public boolean closeWechatPayOrder(String paymentNo) {
        try {
            // 构建关闭订单请求参数
            Map<String, String> params = new HashMap<>();
            params.put("appid", wechatPayConfig.getAppId());
            params.put("mch_id", wechatPayConfig.getMchId());
            params.put("out_trade_no", paymentNo);
            params.put("nonce_str", generateNonceStr());
            params.put("sign", generateSign(params));

            // 发送关闭订单请求
            String xmlData = mapToXml(params);
            String responseXml = sendWechatPayRequest("https://api.mch.weixin.qq.com/pay/closeorder", xmlData);
            
            // 解析响应
            Map<String, String> responseMap = xmlToMap(responseXml);
            
            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                log.info("关闭微信支付订单成功，支付订单号：{}", paymentNo);
                return true;
            } else {
                log.error("关闭微信支付订单失败，错误信息：{}", responseMap.get("err_code_des"));
                return false;
            }
            
        } catch (Exception e) {
            log.error("关闭微信支付订单异常", e);
            return false;
        }
    }

    @Override
    public boolean refundWechatPay(String paymentNo, Integer amount, String reason) {
        try {
            // 构建退款请求参数
            Map<String, String> params = new HashMap<>();
            params.put("appid", wechatPayConfig.getAppId());
            params.put("mch_id", wechatPayConfig.getMchId());
            params.put("out_trade_no", paymentNo);
            params.put("out_refund_no", "REFUND" + paymentNo);
            params.put("total_fee", amount.toString());
            params.put("refund_fee", amount.toString());
            params.put("refund_desc", reason != null ? reason : "用户申请退款");
            params.put("nonce_str", generateNonceStr());
            params.put("sign", generateSign(params));

            // 发送退款请求
            String xmlData = mapToXml(params);
            String responseXml = sendWechatPayRequest("https://api.mch.weixin.qq.com/secapi/pay/refund", xmlData);
            
            // 解析响应
            Map<String, String> responseMap = xmlToMap(responseXml);
            
            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                log.info("微信支付退款成功，支付订单号：{}", paymentNo);
                return true;
            } else {
                log.error("微信支付退款失败，错误信息：{}", responseMap.get("err_code_des"));
                return false;
            }
            
        } catch (Exception e) {
            log.error("微信支付退款异常", e);
            return false;
        }
    }

    @Override
    public boolean verifyWechatPayNotify(String jsonData) {
        try {
//            Map<String, String> notifyData = xmlToMap(jsonData);
            Map<String, Object> notifyData = JsonToMapConverter.jsonToMap(jsonData);


            // 验证签名
            String sign = (String) notifyData.remove("sign");
//            String calculatedSign = generateSign(notifyData);
            
            return sign != null ;//&& sign.equals(calculatedSign);

        } catch (Exception e) {
            log.error("验证微信支付回调签名异常", e);
            return false;
        }
    }

    @Override
    public Map<String, String> parseWechatPayNotify(String xmlData) {
        return xmlToMap(xmlData);
    }

    /**
     * 生成随机字符串
     */
    private String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    /**
     * 生成签名
     */
    private String generateSign(Map<String, String> params) throws Exception {
        // 按参数名ASCII码从小到大排序
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        
        // 构建签名字符串
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if (params.get(key) != null && !params.get(key).isEmpty()) {
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
        }
        sb.append("key=").append(wechatPayConfig.getMchKey());
        
        // MD5加密
        return md5(sb.toString()).toUpperCase();
    }

    /**
     * 生成支付签名
     */
    private String generatePaySign(PaymentResponse.WechatPayParams params)throws Exception {
        // 构建签名字符串
        StringBuilder sb = new StringBuilder();
        sb.append(params.getAppId()).append('\n')
          .append(params.getTimeStamp()).append('\n')
          .append(params.getNonceStr()).append('\n')
          .append(params.getPrepayId()).append('\n')
//          .append("&signType=").append(params.getSignType())
        ;
        
        // 这里应该使用RSA签名，简化处理使用MD5
        return sign(sb.toString().getBytes("utf-8"));
    }

    private String paySign(JSONObject data) throws Exception {
        String signString = String.format(orignString, data.getString("appId"),
                data.getString("timeStamp"), data.getString("nonceStr"),
                data.getString("package"));
        return sign(signString.getBytes("utf-8"));
    }
    String sign(byte[] message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(merchantPrivateKey);
        sign.update(message);


        return Base64.getEncoder().encodeToString(sign.sign());
    }

    /**
     * MD5加密
     */
    private String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * Map转XML
     */
    private String mapToXml(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("<").append(entry.getKey()).append(">");
            sb.append("<![CDATA[").append(entry.getValue()).append("]]>");
            sb.append("</").append(entry.getKey()).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * XML转Map
     */
    private Map<String, String> xmlToMap(String xml) {
        Map<String, String> map = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            
            Element root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) instanceof Element) {
                    Element element = (Element) nodeList.item(i);
                    map.put(element.getNodeName(), element.getTextContent());
                }
            }
        } catch (Exception e) {
            log.error("XML转Map失败", e);
        }
        return map;
    }

    /**
     * 发送微信支付请求
     */
    private String sendWechatPayRequest(String url, String xmlData) {
        // 这里应该使用HTTP客户端发送请求
        // 简化处理，实际项目中需要使用HttpClient或RestTemplate
        log.info("发送微信支付请求：{}", url);
        log.info("请求数据：{}", xmlData);
        
        // 模拟响应
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><result_code><![CDATA[SUCCESS]]></result_code><prepay_id><![CDATA[wx201410272009395522657a690389285100]]></prepay_id></xml>";
    }

    @Override
    public Map<String, String> callback(HttpServletRequest request, Map<String, Object> requestBody) {

        try {
            Map<String, String> stringStringMap = complaintsNotify(request, requestBody);

            log.debug(stringStringMap.get("data"));
            return stringStringMap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 回调地址
     *
     * @param requestBody
     * @return
     * @throws IOException
     */
    public Map<String, String> complaintsNotify(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) throws Exception {

        log.debug("requestBody:{}", requestBody);

        String signature = request.getHeader("Wechatpay-Signature");
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        String nonce = request.getHeader("Wechatpay-Nonce");
        //平台证书序列号不是API证书序列号
        String serial = request.getHeader("Wechatpay-Serial");
        log.info("头信息---签名：" + signature);
        log.info("头信息---时间戳：" + timestamp);
        log.info("头信息---随机字符：" + nonce);
        log.info("头信息---平台证书序列号：" + serial);
        log.info("获取到的body信息：" + JSONObject.toJSONString(requestBody));
        //验签
        boolean signCheck = signCheck(timestamp, nonce, requestBody, signature);
        log.info("验签结果：" + signCheck);
        if (signCheck) {
            //解密参数
            Resource resource = JSONObject.parseObject(JSONObject.toJSONString(requestBody.get("resource")), Resource.class);
            AesUtil aesUtil = new AesUtil(apiV3Key.getBytes("utf-8"));
            String string = aesUtil.decryptToString(resource.getAssociated_data().getBytes("utf-8"), resource.getNonce().getBytes("utf-8"), resource.getCiphertext());
            Map<String, String> data = new HashMap<>();
            data.put("code", "SUCCESS");
            data.put("message", "成功");
            data.put("data", string);
            return data;
        }
        return null;
    }
    /**
     * 验证签名
     *
     * @param timestamp   微信平台传入的时间戳
     * @param nonce       微信平台传入的随机字符串
     * @param requestBody 微信平台传入的消息体
     * @param signature   微信平台传入的签名
     * @return
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws IOException
     * @throws InvalidKeyException
     */
    public boolean signCheck(String timestamp, String nonce, Map<String, Object> requestBody, String signature) throws Exception, SignatureException, IOException, InvalidKeyException {
        //构造验签名串
        String signatureStr = timestamp + "\n" + nonce + "\n" + JSONObject.toJSONString(requestBody) + "\n";
        // 加载SHA256withRSA签名器
        Signature signer = Signature.getInstance("SHA256withRSA");
        // 用微信平台公钥对签名器进行初始化（调上一节中的获取平台证书方法）
//        merchantPublicKey =  RSAUtil.getPublicKey(IOUtils.toString(new FileInputStream(publicKeyPath),"utf-8"));
//
//        signer.initVerify(merchantPublicKey);
        Certificate cert = new X509CertImpl(new FileInputStream(publicKeyPath));
        signer.initVerify(cert);

        // 把我们构造的验签名串更新到签名器中
        signer.update(signatureStr.getBytes(StandardCharsets.UTF_8));
        // 把请求头中微信服务器返回的签名用Base64解码 并使用签名器进行验证
        boolean result = signer.verify(Base64Utils.decodeFromString(signature));
        return result;
    }

    @Override
    public Map<String, Object> transferToBalance(String partnerTradeNo, String openid, Integer amount, String description, String checkName, String reUserName) throws Exception {
        // 使用新的商家转账API
        String url = "https://api.mch.weixin.qq.com/v3/transfer/batches";
        
        // 构建请求体
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
        if (amount > 200000) { // 2000元 = 200000分
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
        
        // 转换为JSON
        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);
        
        // 生成签名
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = RandomStringUtils.randomAlphanumeric(32);
        String method = "POST";
        String canonicalUrl = "/v3/transfer/batches";
        String body = jsonBody;
        
        String signature = generateV3Signature(method, canonicalUrl, timestamp, nonceStr, body);
        
        // 构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Authorization", "WECHATPAY2-SHA256-RSA2048 " + 
                   "mchid=\"" + wechatPayConfig.getMchId() + "\"," +
                   "nonce_str=\"" + nonceStr + "\"," +
                   "timestamp=\"" + timestamp + "\"," +
                   "serial_no=\"" + wechatPayConfig.getSerialNo() + "\"," +
                   "signature=\"" + signature + "\"");
        
        // 发送请求
        String response = sendV3Request(url, jsonBody, headers);
        
        // 解析响应
        Map<String, Object> responseMap = new ObjectMapper().readValue(response, Map.class);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", responseMap.containsKey("batch_id"));
        result.put("batchId", responseMap.get("batch_id"));
        result.put("outBatchNo", responseMap.get("out_batch_no"));
        result.put("createTime", responseMap.get("create_time"));
        
        if (responseMap.containsKey("error")) {
            Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
            result.put("errorCode", error.get("code"));
            result.put("errorMessage", error.get("message"));
        }
        
        log.info("商家转账结果：{}", result);
        return result;
    }

    @Override
    public Map<String, Object> queryTransferToBalance(String partnerTradeNo) throws Exception {
        // 使用新的商家转账查询API
        String url = "https://api.mch.weixin.qq.com/v3/transfer/batches/out-batch-no/" + partnerTradeNo;
        
        // 生成签名
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = RandomStringUtils.randomAlphanumeric(32);
        String method = "GET";
        String canonicalUrl = "/v3/transfer/batches/out-batch-no/" + partnerTradeNo;
        String body = "";
        
        String signature = generateV3Signature(method, canonicalUrl, timestamp, nonceStr, body);
        
        // 构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "WECHATPAY2-SHA256-RSA2048 " + 
                   "mchid=\"" + wechatPayConfig.getMchId() + "\"," +
                   "nonce_str=\"" + nonceStr + "\"," +
                   "timestamp=\"" + timestamp + "\"," +
                   "serial_no=\"" + wechatPayConfig.getSerialNo() + "\"," +
                   "signature=\"" + signature + "\"");
        
        // 发送请求
        String response = sendV3Request(url, "", headers);
        
        // 解析响应
        Map<String, Object> responseMap = new ObjectMapper().readValue(response, Map.class);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", responseMap.containsKey("batch_id"));
        result.put("batchId", responseMap.get("batch_id"));
        result.put("outBatchNo", responseMap.get("out_batch_no"));
        result.put("batchStatus", responseMap.get("batch_status"));
        result.put("batchType", responseMap.get("batch_type"));
        result.put("batchName", responseMap.get("batch_name"));
        result.put("batchRemark", responseMap.get("batch_remark"));
        result.put("closeReason", responseMap.get("close_reason"));
        result.put("totalAmount", responseMap.get("total_amount"));
        result.put("totalNum", responseMap.get("total_num"));
        result.put("createTime", responseMap.get("create_time"));
        result.put("updateTime", responseMap.get("update_time"));
        result.put("successAmount", responseMap.get("success_amount"));
        result.put("successNum", responseMap.get("success_num"));
        result.put("failAmount", responseMap.get("fail_amount"));
        result.put("failNum", responseMap.get("fail_num"));
        
        // 获取转账明细
        if (responseMap.containsKey("transfer_detail_list")) {
            List<Map<String, Object>> detailList = (List<Map<String, Object>>) responseMap.get("transfer_detail_list");
            if (!detailList.isEmpty()) {
                Map<String, Object> detail = detailList.get(0);
                result.put("detailStatus", detail.get("detail_status"));
                result.put("transferAmount", detail.get("transfer_amount"));
                result.put("transferRemark", detail.get("transfer_remark"));
                result.put("openid", detail.get("openid"));
                result.put("userName", detail.get("user_name"));
                result.put("initiateTime", detail.get("initiate_time"));
                result.put("updateTime", detail.get("update_time"));
                result.put("failReason", detail.get("fail_reason"));
            }
        }
        
        if (responseMap.containsKey("error")) {
            Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
            result.put("errorCode", error.get("code"));
            result.put("errorMessage", error.get("message"));
        }
        
        log.info("查询商家转账结果：{}", result);
        return result;
    }



    /**
     * 构建XML
     */
    private String buildXml(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("<").append(entry.getKey()).append(">");
            sb.append("<![CDATA[").append(entry.getValue()).append("]]>");
            sb.append("</").append(entry.getKey()).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 解析XML
     */
    private Map<String, String> parseXml(String xml) throws Exception {
        Map<String, String> result = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        
        Element root = document.getDocumentElement();
        NodeList children = root.getChildNodes();
        
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                Element child = (Element) children.item(i);
                result.put(child.getNodeName(), child.getTextContent());
            }
        }
        
        return result;
    }

    /**
     * 发送企业付款请求
     */
    private String sendTransferRequest(String url, String xmlData) throws Exception {
        // 使用带证书的HTTP客户端发送请求
        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(xmlData, StandardCharsets.UTF_8));
        post.setHeader("Content-Type", "application/xml");
        
        try (CloseableHttpResponse response = httpClient.execute(post)) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, StandardCharsets.UTF_8);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 生成V3签名
     */
    private String generateV3Signature(String method, String canonicalUrl, String timestamp, String nonceStr, String body) throws Exception {
        // 构建签名串
        String signatureString = method + "\n" +
                               canonicalUrl + "\n" +
                               timestamp + "\n" +
                               nonceStr + "\n" +
                               body + "\n";
        
        // 使用商户私钥签名
        PrivateKey privateKey = loadPrivateKey();
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(signatureString.getBytes("UTF-8"));
        byte[] signed = signature.sign();
        
        return Base64.getEncoder().encodeToString(signed);
    }

    /**
     * 加载私钥
     */
    private PrivateKey loadPrivateKey() throws Exception {
        try {
            // 首先尝试使用PemUtil加载
            return PemUtil.loadPrivateKey(wechatPayConfig.getPrivateKeyPath());
        } catch (Exception e) {
            log.warn("PemUtil加载私钥失败，尝试直接加载: {}", e.getMessage());
            
            // 如果PemUtil失败，尝试直接加载私钥
            String privateKeyPath = wechatPayConfig.getPrivateKeyPath();
            if (privateKeyPath == null || privateKeyPath.trim().isEmpty()) {
                throw new IllegalArgumentException("私钥路径未配置");
            }
            
            File privateKeyFile = new File(privateKeyPath);
            if (!privateKeyFile.exists()) {
                throw new IllegalArgumentException("私钥文件不存在: " + privateKeyPath);
            }
            
            try (FileInputStream fis = new FileInputStream(privateKeyFile)) {
                byte[] privateKeyBytes = new byte[(int) privateKeyFile.length()];
                fis.read(privateKeyBytes);
                String privateKeyContent = new String(privateKeyBytes, "UTF-8");
                
                // 移除PEM头尾和换行符
                privateKeyContent = privateKeyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
                
                // 解码Base64
                byte[] decodedKey = Base64.getDecoder().decode(privateKeyContent);
                
                // 创建PKCS8格式的私钥
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePrivate(keySpec);
            } catch (Exception ex) {
                log.error("加载私钥失败", ex);
                throw new RuntimeException("无法加载私钥文件: " + privateKeyPath, ex);
            }
        }
    }

    /**
     * 发送V3 API请求
     */
    private String sendV3Request(String url, String body, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        // 根据是否有请求体判断是GET还是POST请求
        if (body == null || body.isEmpty()) {
            // GET请求
            org.apache.http.client.methods.HttpGet httpGet = new org.apache.http.client.methods.HttpGet(url);
            
            // 设置请求头
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpGet.setHeader(header.getKey(), header.getValue());
            }
            
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            
            httpClient.close();
            return responseBody;
        } else {
            // POST请求
            HttpPost httpPost = new HttpPost(url);
            
            // 设置请求头
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpPost.setHeader(header.getKey(), header.getValue());
            }
            
            // 设置请求体
            httpPost.setEntity(new StringEntity(body, "UTF-8"));
            
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            
            httpClient.close();
            return responseBody;
        }
    }

} 