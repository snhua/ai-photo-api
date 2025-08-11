package com.aiphone.mapper;

import com.aiphone.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 根据用户ID获取订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> getByUserId(@Param("userId") Long userId);
    
    /**
     * 根据绘画师ID获取订单列表
     */
    @Select("SELECT * FROM orders WHERE artist_id = #{artistId} ORDER BY created_at DESC")
    List<Order> getByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 根据状态获取订单列表
     */
    @Select("SELECT * FROM orders WHERE status = #{status} ORDER BY created_at DESC")
    List<Order> getByStatus(@Param("status") String status);
    
    /**
     * 根据订单号获取订单
     */
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Order getByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 获取用户订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId}")
    Long countByUserId(@Param("userId") Long userId);
    
    /**
     * 获取绘画师订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE artist_id = #{artistId}")
    Long countByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 根据状态获取订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status}")
    Long countByStatus(@Param("status") String status);
} 