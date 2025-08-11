package com.aiphone.mapper;

import com.aiphone.entity.Artist;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI绘画师Mapper接口
 */
@Mapper
public interface ArtistMapper extends BaseMapper<Artist> {

    /**
     * 获取推荐AI绘画师列表
     *
     * @param page 分页参数
     * @param category 分类筛选
     * @param sort 排序方式
     * @return 推荐AI绘画师列表
     */
    IPage<Artist> selectRecommendedArtists(Page<Artist> page,
                                          @Param("category") String category,
                                          @Param("sort") String sort);

    /**
     * 获取AI绘画师列表
     *
     * @param page 分页参数
     * @param category 分类筛选
     * @param sort 排序方式
     * @param keyword 搜索关键词
     * @return AI绘画师列表
     */
    IPage<Artist> selectArtistList(Page<Artist> page,
                                  @Param("category") String category,
                                  @Param("sort") String sort,
                                  @Param("keyword") String keyword);

    /**
     * 根据ID获取AI绘画师详情（包含用户信息）
     *
     * @param id 绘画师ID
     * @return AI绘画师详情
     */
    Artist selectArtistDetailById(@Param("id") Long id);

    /**
     * 根据用户ID获取AI绘画师信息
     *
     * @param userId 用户ID
     * @return AI绘画师信息
     */
    Artist selectByUserId(@Param("userId") Long userId);

    /**
     * 更新绘画师评分
     *
     * @param id 绘画师ID
     * @param rating 新评分
     * @return 更新行数
     */
    int updateRating(@Param("id") Long id, @Param("rating") Double rating);

    /**
     * 更新绘画师订单数
     *
     * @param id 绘画师ID
     * @param totalOrders 总订单数
     * @return 更新行数
     */
    int updateTotalOrders(@Param("id") Long id, @Param("totalOrders") Integer totalOrders);
} 