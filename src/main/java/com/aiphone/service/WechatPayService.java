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

    /**
     * 企业付款到零钱
     *
     * @param partnerTradeNo 商户订单号
     * @param openid 用户openid
     * @param amount 付款金额（分）
     * @param description 付款描述
     * @param checkName 校验用户姓名选项：NO_CHECK-不校验真实姓名，FORCE_CHECK-强校验真实姓名，OPTION_CHECK-针对已实名认证的用户才校验真实姓名
     * @param reUserName 收款用户真实姓名（当checkName为FORCE_CHECK或OPTION_CHECK时必填）
     * @return 企业付款结果
     */
    Map<String, Object> transferToBalance(String partnerTradeNo, String openid, Integer amount, String description, String checkName, String reUserName) throws Exception;

    /**
     * 查询企业付款到零钱结果
     *
     * @param partnerTradeNo 商户订单号
     * @return 查询结果
     */
    Map<String, Object> queryTransferToBalance(String partnerTradeNo) throws Exception;
}