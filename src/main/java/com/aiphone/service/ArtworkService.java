package com.aiphone.service;

import com.aiphone.dto.ArtworkDTO;
import com.aiphone.entity.Artwork;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 作品服务接口
 */
public interface ArtworkService extends IService<Artwork> {

    /**
     * 获取热门作品列表
     *
     * @param limit 数量限制
     * @param category 分类筛选
     * @return 热门作品列表
     */
    List<ArtworkDTO> getHotArtworks(Integer limit, String category);

    /**
     * 获取作品列表
     *
     * @param page 页码
     * @param pageSize 每页数量
     * @param category 分类筛选
     * @param sort 排序方式
     * @param keyword 搜索关键词
     * @return 作品列表
     */
    IPage<ArtworkDTO> getArtworkList(Integer page, Integer pageSize, String category, String sort, String keyword);

    /**
     * 根据ID获取作品详情
     *
     * @param id 作品ID
     * @return 作品详情
     */
    ArtworkDTO getArtworkDetail(Long id);

    /**
     * 根据绘画师ID获取作品列表
     *
     * @param artistId 绘画师ID
     * @return 作品列表
     */
    List<ArtworkDTO> getArtworksByArtistId(Long artistId);

    /**
     * 根据分类获取作品列表
     *
     * @param category 作品分类
     * @return 作品列表
     */
    List<ArtworkDTO> getArtworksByCategory(String category);

    /**
     * 搜索作品
     *
     * @param keyword 搜索关键词
     * @return 作品列表
     */
    List<ArtworkDTO> searchArtworks(String keyword);

    /**
     * 创建作品
     *
     * @param artworkDTO 作品信息
     * @return 创建结果
     */
    boolean createArtwork(ArtworkDTO artworkDTO);

    /**
     * 更新作品信息
     *
     * @param id 作品ID
     * @param artworkDTO 作品信息
     * @return 更新结果
     */
    boolean updateArtwork(Long id, ArtworkDTO artworkDTO);

    /**
     * 删除作品
     *
     * @param id 作品ID
     * @return 删除结果
     */
    boolean deleteArtwork(Long id);
} 