package com.aiphone.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付响应DTO
 */
@Data
@ApiModel(description = "支付响应")
public class PaymentResponse {

    @ApiModelProperty(value = "支付订单号", example = "PAY202312010001")
    private String paymentNo;

    @ApiModelProperty(value = "关联订单号", example = "ORDER202312010001")
    private String orderNo;

    @ApiModelProperty(value = "支付金额（元）", example = "99.99")
    private BigDecimal amount;

    @ApiModelProperty(value = "支付方式", example = "WECHAT")
    private String paymentMethod;

    @ApiModelProperty(value = "支付状态", example = "PENDING")
    private String status;

    @ApiModelProperty(value = "微信支付参数")
    private WechatPayParams wechatPayParams;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 微信支付参数
     */
    @Data
    @ApiModel(description = "微信支付参数")
    public static class WechatPayParams {
        @ApiModelProperty(value = "应用ID", example = "wx8888888888888888")
        private String appId;

        @ApiModelProperty(value = "时间戳", example = "1414561699")
        private String timeStamp;

        @ApiModelProperty(value = "随机字符串", example = "5K8264ILTKCH16CQ2502SI8ZNMTM67VS")
        private String nonceStr;

        @ApiModelProperty(value = "预支付交易会话ID", example = "wx201410272009395522657a690389285100")
        private String prepayId;

        @ApiModelProperty(value = "签名类型", example = "RSA")
        private String signType;

        @ApiModelProperty(value = "签名", example = "oR9d8PuhnIc+YZ8cBHFCwfgpaK9gd7vaRvkYD7rthRAZ...")
        private String paySign;
    }
} 