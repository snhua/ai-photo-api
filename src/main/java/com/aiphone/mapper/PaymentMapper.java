package com.aiphone.mapper;

import com.aiphone.entity.Payment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录Mapper接口
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
} 