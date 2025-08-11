package com.aiphone.controller;

import com.aiphone.common.Result;
import com.aiphone.dto.ArtistDTO;
import com.aiphone.dto.ArtworkDTO;
import com.aiphone.service.ArtistService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI绘画师控制器
 */
@Api(tags = "AI绘画师管理")
@RestController
@RequestMapping("/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    /**
     * 获取推荐AI绘画师列表
     */
    @ApiOperation("获取推荐AI绘画师列表")
    @GetMapping("/recommended")
    public Result<IPage<ArtistDTO>> getRecommendedArtists(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("分类筛选") @RequestParam(required = false) String category,
            @ApiParam("排序方式：rating-评分，orders-订单数，price-价格") @RequestParam(required = false) String sort) {
        
        try {
            IPage<ArtistDTO> result = artistService.getRecommendedArtists(page, pageSize, category, sort);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取推荐AI绘画师失败：" + e.getMessage());
        }
    }

    /**
     * 获取AI绘画师列表
     */
    @ApiOperation("获取AI绘画师列表")
    @GetMapping
    public Result<IPage<ArtistDTO>> getArtistList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("分类筛选") @RequestParam(required = false) String category,
            @ApiParam("排序方式：rating-评分，orders-订单数，price-价格") @RequestParam(required = false) String sort,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword) {
        
        try {
            IPage<ArtistDTO> result = artistService.getArtistList(page, pageSize, category, sort, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取AI绘画师列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取AI绘画师详情
     */
    @ApiOperation("获取AI绘画师详情")
    @GetMapping("/{id}")
    public Result<ArtistDTO> getArtistDetail(@ApiParam("绘画师ID") @PathVariable Long id) {
        try {
            ArtistDTO artist = artistService.getArtistDetail(id);
            if (artist == null) {
                return Result.error("AI绘画师不存在");
            }
            return Result.success(artist);
        } catch (Exception e) {
            return Result.error("获取AI绘画师详情失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取AI绘画师信息
     */
    @ApiOperation("根据用户ID获取AI绘画师信息")
    @GetMapping("/user/{userId}")
    public Result<ArtistDTO> getArtistByUserId(@ApiParam("用户ID") @PathVariable Long userId) {
        try {
            ArtistDTO artist = artistService.getArtistByUserId(userId);
            if (artist == null) {
                return Result.error("该用户不是AI绘画师");
            }
            return Result.success(artist);
        } catch (Exception e) {
            return Result.error("获取AI绘画师信息失败：" + e.getMessage());
        }
    }

    /**
     * 获取绘画师作品列表（分页）
     */
    @ApiOperation("获取绘画师作品列表")
    @GetMapping("/{id}/works")
    public Result<IPage<ArtworkDTO>> getArtistWorks(
            @ApiParam("绘画师ID") @PathVariable Long id,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("分类筛选") @RequestParam(required = false) String category,
            @ApiParam("排序方式：latest-最新，price-价格") @RequestParam(required = false) String sort) {
        
        try {
            IPage<ArtworkDTO> result = artistService.getArtistWorks(id, page, pageSize, category, sort);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取绘画师作品失败：" + e.getMessage());
        }
    }

    /**
     * 获取绘画师作品列表（不分页）
     */
    @ApiOperation("获取绘画师作品列表（不分页）")
    @GetMapping("/{id}/works/list")
    public Result<List<ArtworkDTO>> getArtistWorksList(
            @ApiParam("绘画师ID") @PathVariable Long id,
            @ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<ArtworkDTO> result = artistService.getArtistWorksList(id, limit);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取绘画师作品失败：" + e.getMessage());
        }
    }

    /**
     * 创建AI绘画师
     */
    @ApiOperation("创建AI绘画师")
    @PostMapping
    public Result<Boolean> createArtist(@RequestBody ArtistDTO artistDTO) {
        try {
            boolean result = artistService.createArtist(artistDTO);
            if (result) {
                return Result.success(true, "创建AI绘画师成功");
            } else {
                return Result.error("创建AI绘画师失败");
            }
        } catch (Exception e) {
            return Result.error("创建AI绘画师失败：" + e.getMessage());
        }
    }

    /**
     * 更新AI绘画师信息
     */
    @ApiOperation("更新AI绘画师信息")
    @PutMapping("/{id}")
    public Result<Boolean> updateArtist(
            @ApiParam("绘画师ID") @PathVariable Long id,
            @RequestBody ArtistDTO artistDTO) {
        try {
            boolean result = artistService.updateArtist(id, artistDTO);
            if (result) {
                return Result.success(true, "更新AI绘画师成功");
            } else {
                return Result.error("更新AI绘画师失败");
            }
        } catch (Exception e) {
            return Result.error("更新AI绘画师失败：" + e.getMessage());
        }
    }

    /**
     * 删除AI绘画师
     */
    @ApiOperation("删除AI绘画师")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteArtist(@ApiParam("绘画师ID") @PathVariable Long id) {
        try {
            boolean result = artistService.deleteArtist(id);
            if (result) {
                return Result.success(true, "删除AI绘画师成功");
            } else {
                return Result.error("删除AI绘画师失败");
            }
        } catch (Exception e) {
            return Result.error("删除AI绘画师失败：" + e.getMessage());
        }
    }

    /**
     * 更新绘画师评分
     */
    @ApiOperation("更新绘画师评分")
    @PutMapping("/{id}/rating")
    public Result<Boolean> updateRating(
            @ApiParam("绘画师ID") @PathVariable Long id,
            @ApiParam("新评分") @RequestParam Double rating) {
        try {
            if (rating < 0 || rating > 5) {
                return Result.error("评分必须在0-5之间");
            }
            boolean result = artistService.updateRating(id, rating);
            if (result) {
                return Result.success(true, "更新评分成功");
            } else {
                return Result.error("更新评分失败");
            }
        } catch (Exception e) {
            return Result.error("更新评分失败：" + e.getMessage());
        }
    }

    /**
     * 更新绘画师订单数
     */
    @ApiOperation("更新绘画师订单数")
    @PutMapping("/{id}/orders")
    public Result<Boolean> updateTotalOrders(
            @ApiParam("绘画师ID") @PathVariable Long id,
            @ApiParam("总订单数") @RequestParam Integer totalOrders) {
        try {
            if (totalOrders < 0) {
                return Result.error("订单数不能为负数");
            }
            boolean result = artistService.updateTotalOrders(id, totalOrders);
            if (result) {
                return Result.success(true, "更新订单数成功");
            } else {
                return Result.error("更新订单数失败");
            }
        } catch (Exception e) {
            return Result.error("更新订单数失败：" + e.getMessage());
        }
    }
} 