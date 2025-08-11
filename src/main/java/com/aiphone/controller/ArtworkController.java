package com.aiphone.controller;

import com.aiphone.common.Result;
import com.aiphone.dto.ArtworkDTO;
import com.aiphone.service.ArtworkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作品控制器
 */
@Api(tags = "作品管理")
@RestController
@RequestMapping("/artworks")
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;

    /**
     * 获取热门作品列表
     */
    @ApiOperation("获取热门作品列表")
    @GetMapping("/hot")
    public Result<List<ArtworkDTO>> getHotArtworks(
            @ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit,
            @ApiParam("分类筛选") @RequestParam(required = false) String category) {
        
        try {
            List<ArtworkDTO> result = artworkService.getHotArtworks(limit, category);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取热门作品失败：" + e.getMessage());
        }
    }

    /**
     * 获取作品列表
     */
    @ApiOperation("获取作品列表")
    @GetMapping
    public Result<IPage<ArtworkDTO>> getArtworkList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("分类筛选") @RequestParam(required = false) String category,
            @ApiParam("排序方式：latest-最新，price-价格，popular-热门") @RequestParam(required = false) String sort,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword) {
        
        try {
            IPage<ArtworkDTO> result = artworkService.getArtworkList(page, pageSize, category, sort, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取作品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取作品详情
     */
    @ApiOperation("获取作品详情")
    @GetMapping("/{id}")
    public Result<ArtworkDTO> getArtworkDetail(@ApiParam("作品ID") @PathVariable Long id) {
        try {
            ArtworkDTO artwork = artworkService.getArtworkDetail(id);
            if (artwork == null) {
                return Result.error("作品不存在");
            }
            return Result.success(artwork);
        } catch (Exception e) {
            return Result.error("获取作品详情失败：" + e.getMessage());
        }
    }

    /**
     * 根据绘画师ID获取作品列表
     */
    @ApiOperation("根据绘画师ID获取作品列表")
    @GetMapping("/artist/{artistId}")
    public Result<List<ArtworkDTO>> getArtworksByArtistId(@ApiParam("绘画师ID") @PathVariable Long artistId) {
        try {
            List<ArtworkDTO> result = artworkService.getArtworksByArtistId(artistId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取绘画师作品失败：" + e.getMessage());
        }
    }

    /**
     * 根据分类获取作品列表
     */
    @ApiOperation("根据分类获取作品列表")
    @GetMapping("/category/{category}")
    public Result<List<ArtworkDTO>> getArtworksByCategory(@ApiParam("作品分类") @PathVariable String category) {
        try {
            List<ArtworkDTO> result = artworkService.getArtworksByCategory(category);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取分类作品失败：" + e.getMessage());
        }
    }

    /**
     * 搜索作品
     */
    @ApiOperation("搜索作品")
    @GetMapping("/search")
    public Result<List<ArtworkDTO>> searchArtworks(@ApiParam("搜索关键词") @RequestParam String keyword) {
        try {
            List<ArtworkDTO> result = artworkService.searchArtworks(keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("搜索作品失败：" + e.getMessage());
        }
    }
} 