package com.aiphone.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 支付请求DTO
 */
@Data
@ApiModel(description = "支付请求")
public class PaymentRequest {

    @ApiModelProperty(value = "订单号", required = true, example = "ORDER202312010001")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "支付金额（元）", required = true, example = "99.99")
    @NotNull(message = "支付金额不能为空")
    @Positive(message = "支付金额必须大于0")
    private BigDecimal amount;

    @ApiModelProperty(value = "支付方式", required = true, example = "WECHAT", allowableValues = "WECHAT,BALANCE")
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    @ApiModelProperty(value = "支付描述", example = "AI绘画服务费用")
    private String description;

    @ApiModelProperty(value = "客户端IP", example = "127.0.0.1")
    private String clientIp;

    @ApiModelProperty(value = "备注", example = "用户备注信息")
    private String remark;
} 