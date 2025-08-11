package com.aiphone.service.impl;

import com.aiphone.dto.BannerDTO;
import com.aiphone.entity.Banner;
import com.aiphone.mapper.BannerMapper;
import com.aiphone.service.BannerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 横幅服务实现类
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<BannerDTO> getHomeBanners(Integer limit) {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Banner> banners = baseMapper.getHomeBanners(currentTime, limit);
        return banners.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<BannerDTO> getBannersByType(String type) {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Banner> banners = baseMapper.getActiveBannersByType(type, currentTime);
        return banners.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<BannerDTO> getAllActiveBanners() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Banner> banners = baseMapper.getAllActiveBanners(currentTime);
        return banners.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public IPage<BannerDTO> getBannerList(Integer page, Integer pageSize, String type, Integer status) {
        Page<Banner> pageParam = new Page<>(page, pageSize);
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(type)) {
            queryWrapper.eq("type", type);
        }
        
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("sort_weight", "created_at");
        
        IPage<Banner> bannerPage = this.page(pageParam, queryWrapper);
        
        // 转换为DTO
        IPage<BannerDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(bannerPage, dtoPage);
        dtoPage.setRecords(bannerPage.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()));
        
        return dtoPage;
    }

    @Override
    public BannerDTO getBannerDetail(Long id) {
        Banner banner = this.getById(id);
        if (banner == null) {
            return null;
        }
        return convertToDTO(banner);
    }

    @Override
    public boolean createBanner(BannerDTO bannerDTO) {
        Banner banner = convertToEntity(bannerDTO);
        if (banner.getStatus() == null) {
            banner.setStatus(1); // 默认启用
        }
        if (banner.getSortWeight() == null) {
            banner.setSortWeight(0); // 默认排序权重
        }
        return this.save(banner);
    }

    @Override
    public boolean updateBanner(Long id, BannerDTO bannerDTO) {
        Banner existingBanner = this.getById(id);
        if (existingBanner == null) {
            return false;
        }
        
        Banner banner = convertToEntity(bannerDTO);
        banner.setId(id);
        
        return this.updateById(banner);
    }

    @Override
    public boolean deleteBanner(Long id) {
        return this.removeById(id);
    }

    @Override
    public boolean enableBanner(Long id) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setStatus(1);
        return this.updateById(banner);
    }

    @Override
    public boolean disableBanner(Long id) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setStatus(0);
        return this.updateById(banner);
    }

    /**
     * 将实体转换为DTO
     */
    private BannerDTO convertToDTO(Banner banner) {
        BannerDTO dto = new BannerDTO();
        BeanUtils.copyProperties(banner, dto);
        return dto;
    }

    /**
     * 将DTO转换为实体
     */
    private Banner convertToEntity(BannerDTO dto) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(dto, banner);
        return banner;
    }
} 