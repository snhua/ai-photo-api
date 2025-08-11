package com.aiphone.service.impl;

import com.aiphone.dto.ArtistDTO;
import com.aiphone.dto.ArtworkDTO;
import com.aiphone.dto.UserDTO;
import com.aiphone.entity.Artist;
import com.aiphone.entity.Artwork;
import com.aiphone.entity.User;
import com.aiphone.mapper.ArtistMapper;
import com.aiphone.mapper.ArtworkMapper;
import com.aiphone.mapper.UserMapper;
import com.aiphone.service.ArtistService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI绘画师服务实现类
 */
@Service
public class ArtistServiceImpl extends ServiceImpl<ArtistMapper, Artist> implements ArtistService {

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public IPage<ArtistDTO> getRecommendedArtists(Integer page, Integer pageSize, String category, String sort) {
        Page<Artist> pageParam = new Page<>(page, pageSize);
        IPage<Artist> artistPage = artistMapper.selectRecommendedArtists(pageParam, category, sort);
        
        return artistPage.convert(this::convertToDTO);
    }

    @Override
    public IPage<ArtistDTO> getArtistList(Integer page, Integer pageSize, String category, String sort, String keyword) {
        Page<Artist> pageParam = new Page<>(page, pageSize);
        IPage<Artist> artistPage = artistMapper.selectArtistList(pageParam, category, sort, keyword);
        
        return artistPage.convert(this::convertToDTO);
    }

    @Override
    public ArtistDTO getArtistDetail(Long id) {
        Artist artist = artistMapper.selectArtistDetailById(id);
        if (artist == null) {
            return null;
        }
        return convertToDTO(artist);
    }

    @Override
    public ArtistDTO getArtistByUserId(Long userId) {
        Artist artist = artistMapper.selectByUserId(userId);
        if (artist == null) {
            return null;
        }
        return convertToDTO(artist);
    }

    @Override
    public IPage<ArtworkDTO> getArtistWorks(Long artistId, Integer page, Integer pageSize, String category, String sort) {
        Page<Artwork> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Artwork> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Artwork::getArtistId, artistId)
                   .eq(Artwork::getStatus, 1); // 只查询正常状态的作品
        
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(Artwork::getCategory, category);
        }
        
        // 排序
        if ("latest".equals(sort)) {
            queryWrapper.orderByDesc(Artwork::getCreatedAt);
        } else if ("price".equals(sort)) {
            queryWrapper.orderByDesc(Artwork::getPrice);
        } else {
            queryWrapper.orderByDesc(Artwork::getCreatedAt);
        }
        
        IPage<Artwork> artworkPage = artworkMapper.selectPage(pageParam, queryWrapper);
        
        return artworkPage.convert(this::convertArtworkToDTO);
    }

    @Override
    public List<ArtworkDTO> getArtistWorksList(Long artistId, Integer limit) {
        LambdaQueryWrapper<Artwork> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Artwork::getArtistId, artistId)
                   .eq(Artwork::getStatus, 1)
                   .orderByDesc(Artwork::getCreatedAt);
        
        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }
        
        List<Artwork> artworks = artworkMapper.selectList(queryWrapper);
        return artworks.stream().map(this::convertArtworkToDTO).collect(Collectors.toList());
    }

    @Override
    public boolean createArtist(ArtistDTO artistDTO) {
        Artist artist = convertToEntity(artistDTO);
        return save(artist);
    }

    @Override
    public boolean updateArtist(Long id, ArtistDTO artistDTO) {
        Artist artist = convertToEntity(artistDTO);
        artist.setId(id);
        return updateById(artist);
    }

    @Override
    public boolean deleteArtist(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateRating(Long id, Double rating) {
        return artistMapper.updateRating(id, rating) > 0;
    }

    @Override
    public boolean updateTotalOrders(Long id, Integer totalOrders) {
        return artistMapper.updateTotalOrders(id, totalOrders) > 0;
    }

    /**
     * 将实体转换为DTO
     */
    private ArtistDTO convertToDTO(Artist artist) {
        if (artist == null) {
            return null;
        }
        
        ArtistDTO dto = new ArtistDTO();
        BeanUtils.copyProperties(artist, dto);
        
        // 转换专长领域
        if (StringUtils.hasText(artist.getSpecialties())) {
            try {
                List<String> specialties = objectMapper.readValue(artist.getSpecialties(), new TypeReference<List<String>>() {});
                dto.setSpecialties(specialties);
            } catch (Exception e) {
                // 如果JSON解析失败，按逗号分隔
                String[] specialties = artist.getSpecialties().split(",");
                dto.setSpecialties(Arrays.asList(specialties));
            }
        }
        
        // 转换用户信息
        if (artist.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(artist.getUser(), userDTO);
            dto.setUser(userDTO);
        }
        
        return dto;
    }

    /**
     * 将DTO转换为实体
     */
    private Artist convertToEntity(ArtistDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Artist artist = new Artist();
        BeanUtils.copyProperties(dto, artist);
        
        // 转换专长领域
        if (dto.getSpecialties() != null && !dto.getSpecialties().isEmpty()) {
            try {
                String specialties = objectMapper.writeValueAsString(dto.getSpecialties());
                artist.setSpecialties(specialties);
            } catch (Exception e) {
                // 如果JSON序列化失败，按逗号连接
                String specialties = String.join(",", dto.getSpecialties());
                artist.setSpecialties(specialties);
            }
        }
        
        return artist;
    }

    /**
     * 将作品实体转换为DTO
     */
    private ArtworkDTO convertArtworkToDTO(Artwork artwork) {
        if (artwork == null) {
            return null;
        }
        
        ArtworkDTO dto = new ArtworkDTO();
        BeanUtils.copyProperties(artwork, dto);
        
        // 处理标签，将逗号分隔的字符串转换为List
        if (StringUtils.hasText(artwork.getTags())) {
            dto.setTags(Arrays.asList(artwork.getTags().split(",")));
        }
        
        return dto;
    }
} 