package com.aiphone.controller;

import com.aiphone.common.Result;
import com.aiphone.dto.PaymentRequest;
import com.aiphone.dto.PaymentResponse;
import com.aiphone.entity.Payment;
import com.aiphone.service.PaymentService;
import com.aiphone.security.SecurityUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/payment")
@Api(tags = "支付相关接口")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    @ApiOperation("创建支付订单")
    public Result<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request,
            HttpServletRequest httpRequest) {
        try {
            // 获取当前用户ID
            Long userId = SecurityUtils.getCurrentUserId();
            
            // 设置客户端IP
            if (request.getClientIp() == null || request.getClientIp().isEmpty()) {
                request.setClientIp(getClientIp(httpRequest));
            }
            
            PaymentResponse response = paymentService.createPayment(request, userId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建支付订单失败", e);
            return Result.error(1005, "创建支付订单失败：" + e.getMessage());
        }
    }

    /**
     * 查询支付订单
     */
    @GetMapping("/{paymentNo}")
    @ApiOperation("查询支付订单")
    public Result<Payment> getPayment(
            @ApiParam("支付订单号") @PathVariable String paymentNo) {
        try {
            Payment payment = paymentService.getPaymentByNo(paymentNo);
            if (payment == null) {
                return Result.error(1004, "支付订单不存在");
            }
            return Result.success(payment);
        } catch (Exception e) {
            log.error("查询支付订单失败", e);
            return Result.error(1005, "查询支付订单失败：" + e.getMessage());
        }
    }

    /**
     * 查询用户支付记录
     */
    @GetMapping("/user")
    @ApiOperation("查询用户支付记录")
    public Result<IPage<Payment>> getUserPayments(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            Page<Payment> page = new Page<>(current, size);
            IPage<Payment> payments = paymentService.getUserPayments(userId, page);
            return Result.success(payments);
        } catch (Exception e) {
            log.error("查询用户支付记录失败", e);
            return Result.error(1005, "查询用户支付记录失败：" + e.getMessage());
        }
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/{paymentNo}/status")
    @ApiOperation("查询支付状态")
    public Result<String> getPaymentStatus(
            @ApiParam("支付订单号") @PathVariable String paymentNo) {
        try {
            String status = paymentService.queryPaymentStatus(paymentNo);
            return Result.success(status);
        } catch (Exception e) {
            log.error("查询支付状态失败", e);
            return Result.error(1005, "查询支付状态失败：" + e.getMessage());
        }
    }

    /**
     * 取消支付订单
     */
    @PostMapping("/{paymentNo}/cancel")
    @ApiOperation("取消支付订单")
    public Result<Boolean> cancelPayment(
            @ApiParam("支付订单号") @PathVariable String paymentNo) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            boolean success = paymentService.cancelPayment(paymentNo, userId);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error(1004, "取消支付订单失败");
            }
        } catch (Exception e) {
            log.error("取消支付订单失败", e);
            return Result.error(1005, "取消支付订单失败：" + e.getMessage());
        }
    }

    /**
     * 申请退款
     */
    @PostMapping("/{paymentNo}/refund")
    @ApiOperation("申请退款")
    public Result<Boolean> refund(
            @ApiParam("支付订单号") @PathVariable String paymentNo,
            @ApiParam("退款金额（分）") @RequestParam Integer amount,
            @ApiParam("退款原因") @RequestParam(required = false) String reason) {
        try {
            boolean success = paymentService.refund(paymentNo, amount, reason);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error(1004, "申请退款失败");
            }
        } catch (Exception e) {
            log.error("申请退款失败", e);
            return Result.error(1005, "申请退款失败：" + e.getMessage());
        }
    }

    @RequestMapping("notify")
    public String callback(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) throws ParseException {
        return paymentService.callback(request, requestBody);
    }

    /**
     * 微信支付回调
     */
    @PostMapping("/wechat/notify")
    @ApiOperation("微信支付回调")
    public String wechatPayNotify(HttpServletRequest request) {
        try {
            // 读取回调数据
            String xmlData = readRequestBody(request);
            log.info("收到微信支付回调：{}", xmlData);
            
            // 处理回调
            String result = paymentService.handleWechatPayNotify(xmlData);
            log.info("微信支付回调处理结果：{}", result);
            
            return result;
        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 读取请求体
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
} 