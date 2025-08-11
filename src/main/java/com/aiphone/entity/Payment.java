package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("payments")
public class Payment {

    /**
     * 支付记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 支付金额
     */
    private BigDecimal amountYuan;

    /**
     * 支付方式：wechat-微信支付，balance-余额支付
     */
    private String paymentMethod;

    /**
     * 支付状态：pending-待支付，success-支付成功，failed-支付失败，refunded-已退款
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付金额（分）
     */
    private Integer amount;

    /**
     * 支付描述
     */
    private String description;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
} 