package com.aiphone.mapper;

import com.aiphone.entity.Review;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 评价Mapper接口
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    
    /**
     * 根据绘画师ID获取评价列表
     */
    @Select("SELECT * FROM reviews WHERE artist_id = #{artistId} ORDER BY created_at DESC")
    List<Review> getByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 根据用户ID获取评价列表
     */
    @Select("SELECT * FROM reviews WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Review> getByUserId(@Param("userId") Long userId);
    
    /**
     * 根据订单ID获取评价
     */
    @Select("SELECT * FROM reviews WHERE order_id = #{orderId}")
    Review getByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 获取绘画师平均评分
     */
    @Select("SELECT AVG(rating) FROM reviews WHERE artist_id = #{artistId}")
    BigDecimal getAverageRating(@Param("artistId") Long artistId);
    
    /**
     * 获取绘画师评价数量
     */
    @Select("SELECT COUNT(*) FROM reviews WHERE artist_id = #{artistId}")
    Long countByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 检查订单是否已评价
     */
    @Select("SELECT COUNT(*) FROM reviews WHERE order_id = #{orderId}")
    Long countByOrderId(@Param("orderId") Long orderId);
} 