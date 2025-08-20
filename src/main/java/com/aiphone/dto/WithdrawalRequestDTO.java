package com.aiphone.dto;

import lombok.Data;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 提现申请DTO
 */
@Data
public class WithdrawalRequestDTO {
    
    /**
     * 提现金额
     */
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.01", message = "提现金额必须大于0")
    private BigDecimal amount;
    
    /**
     * 提现方式：wechat-微信，alipay-支付宝，bank-银行卡
     */
    @NotBlank(message = "提现方式不能为空")
    private String paymentMethod;
    
    /**
     * 收款账户
     */
    @NotBlank(message = "收款账户不能为空")
    private String paymentAccount;
    
    /**
     * 备注信息
     */
    private String remark;
}
