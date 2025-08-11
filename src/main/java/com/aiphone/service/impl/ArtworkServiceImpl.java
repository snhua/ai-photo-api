package com.aiphone.service.impl;

import com.aiphone.dto.ArtworkDTO;
import com.aiphone.entity.Artwork;
import com.aiphone.mapper.ArtworkMapper;
import com.aiphone.service.ArtworkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作品服务实现类
 */
@Service
public class ArtworkServiceImpl extends ServiceImpl<ArtworkMapper, Artwork> implements ArtworkService {

    @Override
    public List<ArtworkDTO> getHotArtworks(Integer limit, String category) {
        QueryWrapper<Artwork> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只查询正常状态的作品
        
        if (StringUtils.hasText(category)) {
            queryWrapper.eq("category", category);
        }
        
        // 按创建时间倒序排列，获取最新的作品作为热门作品
        queryWrapper.orderByDesc("created_at");
        queryWrapper.last("LIMIT " + limit);
        
        List<Artwork> artworks = this.list(queryWrapper);
        return artworks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public IPage<ArtworkDTO> getArtworkList(Integer page, Integer pageSize, String category, String sort, String keyword) {
        Page<Artwork> pageParam = new Page<>(page, pageSize);
        QueryWrapper<Artwork> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只查询正常状态的作品
        
        if (StringUtils.hasText(category)) {
            queryWrapper.eq("category", category);
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("title", keyword)
                .or()
                .like("description", keyword)
                .or()
                .like("tags", keyword)
            );
        }
        
        // 排序
        if ("latest".equals(sort)) {
            queryWrapper.orderByDesc("created_at");
        } else if ("price".equals(sort)) {
            queryWrapper.orderByDesc("price");
        } else if ("popular".equals(sort)) {
            // 这里可以根据实际需求实现热门排序逻辑
            // 比如根据浏览量、点赞数等
            queryWrapper.orderByDesc("created_at");
        } else {
            queryWrapper.orderByDesc("created_at");
        }
        
        IPage<Artwork> artworkPage = this.page(pageParam, queryWrapper);
        
        // 转换为DTO
        IPage<ArtworkDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(artworkPage, dtoPage);
        dtoPage.setRecords(artworkPage.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()));
        
        return dtoPage;
    }

    @Override
    public ArtworkDTO getArtworkDetail(Long id) {
        Artwork artwork = this.getById(id);
        if (artwork == null || artwork.getStatus() == 0) {
            return null;
        }
        return convertToDTO(artwork);
    }

    @Override
    public List<ArtworkDTO> getArtworksByArtistId(Long artistId) {
        List<Artwork> artworks = baseMapper.getByArtistId(artistId);
        return artworks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ArtworkDTO> getArtworksByCategory(String category) {
        List<Artwork> artworks = baseMapper.getByCategory(category);
        return artworks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ArtworkDTO> searchArtworks(String keyword) {
        List<Artwork> artworks = baseMapper.searchByTag(keyword);
        return artworks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public boolean createArtwork(ArtworkDTO artworkDTO) {
        Artwork artwork = convertToEntity(artworkDTO);
        artwork.setStatus(1); // 设置为正常状态
        return this.save(artwork);
    }

    @Override
    public boolean updateArtwork(Long id, ArtworkDTO artworkDTO) {
        Artwork existingArtwork = this.getById(id);
        if (existingArtwork == null) {
            return false;
        }
        
        Artwork artwork = convertToEntity(artworkDTO);
        artwork.setId(id);
        artwork.setStatus(existingArtwork.getStatus()); // 保持原有状态
        
        return this.updateById(artwork);
    }

    @Override
    public boolean deleteArtwork(Long id) {
        // 软删除，将状态设置为0
        Artwork artwork = new Artwork();
        artwork.setId(id);
        artwork.setStatus(0);
        return this.updateById(artwork);
    }

    /**
     * 将实体转换为DTO
     */
    private ArtworkDTO convertToDTO(Artwork artwork) {
        ArtworkDTO dto = new ArtworkDTO();
        BeanUtils.copyProperties(artwork, dto);
        
        // 处理标签，将逗号分隔的字符串转换为List
        if (StringUtils.hasText(artwork.getTags())) {
            dto.setTags(Arrays.asList(artwork.getTags().split(",")));
        }
        
        return dto;
    }

    /**
     * 将DTO转换为实体
     */
    private Artwork convertToEntity(ArtworkDTO dto) {
        Artwork artwork = new Artwork();
        BeanUtils.copyProperties(dto, artwork);
        
        // 处理标签，将List转换为逗号分隔的字符串
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            artwork.setTags(String.join(",", dto.getTags()));
        }
        
        return artwork;
    }
} 