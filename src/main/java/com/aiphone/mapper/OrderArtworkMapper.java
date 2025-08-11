package com.aiphone.mapper;

import com.aiphone.entity.OrderArtwork;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单作品Mapper接口
 */
@Mapper
public interface OrderArtworkMapper extends BaseMapper<OrderArtwork> {
    
    /**
     * 根据订单ID获取作品列表
     */
    @Select("SELECT * FROM order_artworks WHERE order_id = #{orderId} ORDER BY created_at DESC")
    List<OrderArtwork> getByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 获取订单作品数量
     */
    @Select("SELECT COUNT(*) FROM order_artworks WHERE order_id = #{orderId}")
    Long countByOrderId(@Param("orderId") Long orderId);
} 