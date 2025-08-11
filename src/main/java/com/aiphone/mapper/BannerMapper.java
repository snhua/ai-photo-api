package com.aiphone.mapper;

import com.aiphone.entity.Banner;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 横幅Mapper接口
 */
@Mapper
public interface BannerMapper extends BaseMapper<Banner> {
    
    /**
     * 根据类型获取有效的横幅列表
     */
    @Select("SELECT * FROM banners WHERE type = #{type} AND status = 1 " +
            "AND (start_time IS NULL OR start_time <= #{currentTime}) " +
            "AND (end_time IS NULL OR end_time >= #{currentTime}) " +
            "ORDER BY sort_weight DESC, created_at DESC")
    List<Banner> getActiveBannersByType(@Param("type") String type, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 获取所有有效的横幅列表
     */
    @Select("SELECT * FROM banners WHERE status = 1 " +
            "AND (start_time IS NULL OR start_time <= #{currentTime}) " +
            "AND (end_time IS NULL OR end_time >= #{currentTime}) " +
            "ORDER BY sort_weight DESC, created_at DESC")
    List<Banner> getAllActiveBanners(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 根据类型获取横幅列表（包含禁用状态）
     */
    @Select("SELECT * FROM banners WHERE type = #{type} ORDER BY sort_weight DESC, created_at DESC")
    List<Banner> getBannersByType(@Param("type") String type);
    
    /**
     * 获取首页横幅
     */
    @Select("SELECT * FROM banners WHERE type = 'home' AND status = 1 " +
            "AND (start_time IS NULL OR start_time <= #{currentTime}) " +
            "AND (end_time IS NULL OR end_time >= #{currentTime}) " +
            "ORDER BY sort_weight DESC, created_at DESC LIMIT #{limit}")
    List<Banner> getHomeBanners(@Param("currentTime") LocalDateTime currentTime, @Param("limit") Integer limit);
} 