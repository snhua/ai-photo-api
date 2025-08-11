package com.aiphone.mapper;

import com.aiphone.entity.Artwork;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 作品Mapper接口
 */
@Mapper
public interface ArtworkMapper extends BaseMapper<Artwork> {
    
    /**
     * 根据绘画师ID获取作品列表
     */
    @Select("SELECT * FROM artworks WHERE artist_id = #{artistId} AND status = 1 ORDER BY created_at DESC")
    List<Artwork> getByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 根据分类获取作品列表
     */
    @Select("SELECT * FROM artworks WHERE category = #{category} AND status = 1 ORDER BY created_at DESC")
    List<Artwork> getByCategory(@Param("category") String category);
    
    /**
     * 根据标签搜索作品
     */
    @Select("SELECT * FROM artworks WHERE tags LIKE CONCAT('%', #{tag}, '%') AND status = 1")
    List<Artwork> searchByTag(@Param("tag") String tag);
    
    /**
     * 获取推荐作品
     */
    @Select("SELECT * FROM artworks WHERE status = 1 ORDER BY created_at DESC LIMIT #{limit}")
    List<Artwork> getRecommendedArtworks(@Param("limit") Integer limit);
    
    /**
     * 获取绘画师作品数量
     */
    @Select("SELECT COUNT(*) FROM artworks WHERE artist_id = #{artistId} AND status = 1")
    Long countByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 获取热门作品列表
     */
    @Select("SELECT * FROM artworks WHERE status = 1 ORDER BY created_at DESC LIMIT #{limit}")
    List<Artwork> getHotArtworks(@Param("limit") Integer limit);
    
    /**
     * 根据分类获取热门作品列表
     */
    @Select("SELECT * FROM artworks WHERE category = #{category} AND status = 1 ORDER BY created_at DESC LIMIT #{limit}")
    List<Artwork> getHotArtworksByCategory(@Param("category") String category, @Param("limit") Integer limit);
} 