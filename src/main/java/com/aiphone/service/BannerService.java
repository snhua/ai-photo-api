package com.aiphone.service;

import com.aiphone.dto.BannerDTO;
import com.aiphone.entity.Banner;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 横幅服务接口
 */
public interface BannerService extends IService<Banner> {

    /**
     * 获取首页横幅列表
     *
     * @param limit 数量限制
     * @return 横幅列表
     */
    List<BannerDTO> getHomeBanners(Integer limit);

    /**
     * 根据类型获取横幅列表
     *
     * @param type 横幅类型
     * @return 横幅列表
     */
    List<BannerDTO> getBannersByType(String type);

    /**
     * 获取所有有效的横幅列表
     *
     * @return 横幅列表
     */
    List<BannerDTO> getAllActiveBanners();

    /**
     * 获取横幅列表（分页）
     *
     * @param page 页码
     * @param pageSize 每页数量
     * @param type 横幅类型
     * @param status 状态
     * @return 横幅列表
     */
    IPage<BannerDTO> getBannerList(Integer page, Integer pageSize, String type, Integer status);

    /**
     * 根据ID获取横幅详情
     *
     * @param id 横幅ID
     * @return 横幅详情
     */
    BannerDTO getBannerDetail(Long id);

    /**
     * 创建横幅
     *
     * @param bannerDTO 横幅信息
     * @return 创建结果
     */
    boolean createBanner(BannerDTO bannerDTO);

    /**
     * 更新横幅信息
     *
     * @param id 横幅ID
     * @param bannerDTO 横幅信息
     * @return 更新结果
     */
    boolean updateBanner(Long id, BannerDTO bannerDTO);

    /**
     * 删除横幅
     *
     * @param id 横幅ID
     * @return 删除结果
     */
    boolean deleteBanner(Long id);

    /**
     * 启用横幅
     *
     * @param id 横幅ID
     * @return 操作结果
     */
    boolean enableBanner(Long id);

    /**
     * 禁用横幅
     *
     * @param id 横幅ID
     * @return 操作结果
     */
    boolean disableBanner(Long id);
} 