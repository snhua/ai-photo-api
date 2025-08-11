package com.aiphone.service;

import com.aiphone.dto.ArtistDTO;
import com.aiphone.dto.ArtworkDTO;
import com.aiphone.entity.Artist;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * AI绘画师服务接口
 */
public interface ArtistService extends IService<Artist> {

    /**
     * 获取推荐AI绘画师列表
     *
     * @param page 页码
     * @param pageSize 每页数量
     * @param category 分类筛选
     * @param sort 排序方式
     * @return 推荐AI绘画师列表
     */
    IPage<ArtistDTO> getRecommendedArtists(Integer page, Integer pageSize, String category, String sort);

    /**
     * 获取AI绘画师列表
     *
     * @param page 页码
     * @param pageSize 每页数量
     * @param category 分类筛选
     * @param sort 排序方式
     * @param keyword 搜索关键词
     * @return AI绘画师列表
     */
    IPage<ArtistDTO> getArtistList(Integer page, Integer pageSize, String category, String sort, String keyword);

    /**
     * 根据ID获取AI绘画师详情
     *
     * @param id 绘画师ID
     * @return AI绘画师详情
     */
    ArtistDTO getArtistDetail(Long id);

    /**
     * 根据用户ID获取AI绘画师信息
     *
     * @param userId 用户ID
     * @return AI绘画师信息
     */
    ArtistDTO getArtistByUserId(Long userId);

    /**
     * 获取绘画师作品列表
     *
     * @param artistId 绘画师ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param category 分类筛选
     * @param sort 排序方式
     * @return 作品列表
     */
    IPage<ArtworkDTO> getArtistWorks(Long artistId, Integer page, Integer pageSize, String category, String sort);

    /**
     * 获取绘画师作品列表（不分页）
     *
     * @param artistId 绘画师ID
     * @param limit 数量限制
     * @return 作品列表
     */
    List<ArtworkDTO> getArtistWorksList(Long artistId, Integer limit);

    /**
     * 创建AI绘画师
     *
     * @param artistDTO AI绘画师信息
     * @return 创建结果
     */
    boolean createArtist(ArtistDTO artistDTO);

    /**
     * 更新AI绘画师信息
     *
     * @param id 绘画师ID
     * @param artistDTO AI绘画师信息
     * @return 更新结果
     */
    boolean updateArtist(Long id, ArtistDTO artistDTO);

    /**
     * 删除AI绘画师
     *
     * @param id 绘画师ID
     * @return 删除结果
     */
    boolean deleteArtist(Long id);

    /**
     * 更新绘画师评分
     *
     * @param id 绘画师ID
     * @param rating 新评分
     * @return 更新结果
     */
    boolean updateRating(Long id, Double rating);

    /**
     * 更新绘画师订单数
     *
     * @param id 绘画师ID
     * @param totalOrders 总订单数
     * @return 更新结果
     */
    boolean updateTotalOrders(Long id, Integer totalOrders);
} 