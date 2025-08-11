package com.aiphone.service;

import com.aiphone.dto.PaymentResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信支付服务接口
 */
public interface WechatPayService {

    /**
     * 创建微信支付订单
     *
     * @param paymentNo  支付订单号
     * @param amount     支付金额（元）
     * @param description 支付描述
     * @param clientIp   客户端IP
     * @return 支付响应
     */
    PaymentResponse.WechatPayParams createWechatPayOrder(String paymentNo, BigDecimal amount, String description, String clientIp) throws Exception;

    /**
     * 查询微信支付订单状态
     *
     * @param paymentNo 支付订单号
     * @return 支付状态
     */
    String queryWechatPayStatus(String paymentNo);

    /**
     * 关闭微信支付订单
     *
     * @param paymentNo 支付订单号
     * @return 是否成功
     */
    boolean closeWechatPayOrder(String paymentNo);

    /**
     * 申请微信支付退款
     *
     * @param paymentNo 支付订单号
     * @param amount    退款金额（分）
     * @param reason    退款原因
     * @return 是否成功
     */
    boolean refundWechatPay(String paymentNo, Integer amount, String reason);

    /**
     * 验证微信支付回调签名
     *
     * @param xmlData 回调XML数据
     * @return 是否验证通过
     */
    boolean verifyWechatPayNotify(String xmlData);

    /**
     * 解析微信支付回调数据
     *
     * @param xmlData 回调XML数据
     * @return 回调数据Map
     */
    java.util.Map<String, String> parseWechatPayNotify(String xmlData);

    Map callback(HttpServletRequest request, Map<String, Object> requestBody);
}