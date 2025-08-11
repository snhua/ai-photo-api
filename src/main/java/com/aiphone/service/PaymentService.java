package com.aiphone.service;

import com.aiphone.dto.PaymentRequest;
import com.aiphone.dto.PaymentResponse;
import com.aiphone.entity.Payment;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付订单
     *
     * @param request 支付请求
     * @param userId  用户ID
     * @return 支付响应
     */
    PaymentResponse createPayment(PaymentRequest request, Long userId);

    /**
     * 查询支付订单
     *
     * @param paymentNo 支付订单号
     * @return 支付订单
     */
    Payment getPaymentByNo(String paymentNo);

    /**
     * 查询用户支付记录
     *
     * @param userId 用户ID
     * @param page   分页参数
     * @return 支付记录列表
     */
    IPage<Payment> getUserPayments(Long userId, Page<Payment> page);

    /**
     * 查询所有支付记录
     *
     * @param page 分页参数
     * @return 支付记录列表
     */
    IPage<Payment> getAllPayments(Page<Payment> page);

    /**
     * 处理微信支付回调
     *
     * @param xmlData 回调XML数据
     * @return 处理结果
     */
    String handleWechatPayNotify(String xmlData);

    /**
     * 查询支付状态
     *
     * @param paymentNo 支付订单号
     * @return 支付状态
     */
    String queryPaymentStatus(String paymentNo);

    /**
     * 取消支付订单
     *
     * @param paymentNo 支付订单号
     * @param userId    用户ID
     * @return 是否成功
     */
    boolean cancelPayment(String paymentNo, Long userId);

    /**
     * 申请退款
     *
     * @param paymentNo 支付订单号
     * @param amount    退款金额（分）
     * @param reason    退款原因
     * @return 是否成功
     */
    boolean refund(String paymentNo, Integer amount, String reason);

    String callback(HttpServletRequest request, Map<String, Object> requestBody);
} 