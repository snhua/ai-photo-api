package com.aiphone.service.impl;

import com.aiphone.dto.PaymentRequest;
import com.aiphone.dto.PaymentResponse;
import com.aiphone.entity.Order;
import com.aiphone.entity.Payment;
import com.aiphone.service.PaymentService;
import com.aiphone.service.WechatPayService;
import com.aiphone.service.UserService;
import com.aiphone.service.OrderService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiphone.mapper.PaymentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 支付服务实现类
 */
@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

  final   String success = "{   \n" +
            "    \"code\": \"SUCCESS\",\n" +
            "    \"message\": \"成功\"\n" +
            "}";
    final String fail = "{   \n" +
            "    \"code\": \"FAIL\",\n" +
            "    \"message\": \"失败\"\n" +
            "}";

    @Autowired
    private WechatPayService wechatPayService;


    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, Long userId) {
        try {
            // 生成支付订单号
            String paymentNo = generatePaymentNo();
            
            // 创建支付记录
            Payment payment = new Payment();
            payment.setPaymentNo(paymentNo);
            payment.setOrderNo(request.getOrderNo());
            payment.setUserId(userId);
            payment.setAmount(request.getAmount().multiply(new BigDecimal("100")).intValue()); // 转换为分
            payment.setAmountYuan(request.getAmount());
            payment.setPaymentMethod(request.getPaymentMethod().toLowerCase()); // 转换为小写以匹配数据库枚举
            payment.setStatus("pending"); // 使用小写状态
            payment.setDescription(request.getDescription());
            payment.setClientIp(request.getClientIp());
            payment.setRemark(request.getRemark());
            payment.setCreateTime(LocalDateTime.now());
            payment.setUpdateTime(LocalDateTime.now());
            
            // 设置order_id - 根据orderNo查询订单ID
            Long orderId = orderService.getOrderIdByOrderNo(request.getOrderNo());
            payment.setOrderId(orderId);
            
            // 保存支付记录
            save(payment);
            
            // 根据支付方式创建支付订单
            PaymentResponse response = new PaymentResponse();
            response.setPaymentNo(paymentNo);
            response.setOrderNo(request.getOrderNo());
            response.setAmount(request.getAmount());
            response.setPaymentMethod(request.getPaymentMethod());
            response.setStatus("PENDING");
            response.setCreateTime(payment.getCreateTime());
            
            if ("WECHAT".equalsIgnoreCase(request.getPaymentMethod())) {
                // 创建微信支付订单
                PaymentResponse.WechatPayParams wechatPayParams = wechatPayService.createWechatPayOrder(
                    paymentNo, 
                    request.getAmount(), 
                    request.getDescription(), 
                    request.getClientIp()
                );
                response.setWechatPayParams(wechatPayParams);
            }
            
            log.info("创建支付订单成功，支付订单号：{}", paymentNo);
            return response;
            
        } catch (Exception e) {
            log.error("创建支付订单失败", e);
            e.printStackTrace();
            throw new RuntimeException("创建支付订单失败：" + e.getMessage());
        }
    }

    @Override
    public Payment getPaymentByNo(String paymentNo) {
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("payment_no", paymentNo);
        return getOne(queryWrapper);
    }

    @Override
    public IPage<Payment> getUserPayments(Long userId, Page<Payment> page) {
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return page(page, queryWrapper);
    }

    @Override
    public IPage<Payment> getAllPayments(Page<Payment> page) {
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return page(page, queryWrapper);
    }

    public String callback(HttpServletRequest request, Map<String, Object> requestBody)  {
        Map map = wechatPayService.callback(request, requestBody);
        if (map == null)
            return fail;
        String data = (String) map.get("data");
        JSONObject dataJson = JSON.parseObject(data);
        String out_trade_no = dataJson.getString("out_trade_no");
        if (out_trade_no == null) {
            return fail;
        }
        Payment payment = getPaymentByNo(out_trade_no);

        if (payment == null) {
            return fail;
        }
        if (payment.getStatus() =="PAID") {
            return success;
        }
        String transaction_id = dataJson.getString("transaction_id");
        String trade_state = dataJson.getString("trade_state");



//        mkOrderMapper.updateByPrimaryKeySelective(updateMkOrder);
        if ("SUCCESS".equals(trade_state)) {
            Order updateMkOrder = orderService.getById(payment.getOrderId());
//        updateMkOrder.setStatus("PAID");
            orderService.updateOrderStatus(payment.getOrderId(),"PAID");
        }

        return success;
    }


    @Override
    @Transactional
    public String handleWechatPayNotify(String xmlData) {
        try {
            // 验证回调签名
            if (!wechatPayService.verifyWechatPayNotify(xmlData)) {
                log.error("微信支付回调签名验证失败");
                return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[签名验证失败]]></return_msg></xml>";
            }
            
            // 解析回调数据
            Map<String, String> notifyData = wechatPayService.parseWechatPayNotify(xmlData);
            
            String paymentNo = notifyData.get("out_trade_no");
            String transactionId = notifyData.get("transaction_id");
            String resultCode = notifyData.get("result_code");
            
            // 查询支付记录
            Payment payment = getPaymentByNo(paymentNo);
            if (payment == null) {
                log.error("支付记录不存在，支付订单号：{}", paymentNo);
                return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[支付记录不存在]]></return_msg></xml>";
            }
            
            // 检查支付状态
            if ("success".equals(payment.getStatus())) {
                log.info("支付订单已处理，支付订单号：{}", paymentNo);
                return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            }
            
            if ("SUCCESS".equals(resultCode)) {
                // 支付成功
                payment.setStatus("success");
                payment.setUpdateTime(LocalDateTime.now());
                updateById(payment);
                
                // 处理订单状态（这里可以调用订单服务更新订单状态）
                orderService.updateOrderStatus(payment.getOrderNo(), "PAID");
                
                log.info("微信支付成功，支付订单号：{}，微信交易号：{}", paymentNo, transactionId);
            } else {
                // 支付失败
                payment.setStatus("failed");
                payment.setUpdateTime(LocalDateTime.now());
                updateById(payment);
                
                log.error("微信支付失败，支付订单号：{}", paymentNo);
            }
            
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            
        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        }
    }

    @Override
    public String queryPaymentStatus(String paymentNo) {
        Payment payment = getPaymentByNo(paymentNo);
        if (payment == null) {
            return "NOT_FOUND";
        }
        return payment.getStatus();
    }

    @Override
    @Transactional
    public boolean cancelPayment(String paymentNo, Long userId) {
        Payment payment = getPaymentByNo(paymentNo);
        if (payment == null || !payment.getUserId().equals(userId)) {
            return false;
        }
        
        if (!"pending".equals(payment.getStatus())) {
            return false;
        }
        
        payment.setStatus("failed");
        payment.setUpdateTime(LocalDateTime.now());
        updateById(payment);
        
        log.info("取消支付订单成功，支付订单号：{}", paymentNo);
        return true;
    }

    @Override
    @Transactional
    public boolean refund(String paymentNo, Integer amount, String reason) {
        Payment payment = getPaymentByNo(paymentNo);
        if (payment == null || !"success".equals(payment.getStatus())) {
            return false;
        }
        
        if ("wechat".equals(payment.getPaymentMethod())) {
            boolean refundSuccess = wechatPayService.refundWechatPay(paymentNo, amount, reason);
            if (refundSuccess) {
                payment.setStatus("refunded");
                payment.setUpdateTime(LocalDateTime.now());
                updateById(payment);
                return true;
            }
        }
        
        return false;
    }

    /**
     * 生成支付订单号
     */
    private String generatePaymentNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "PAY" + timestamp + random;
    }
} 