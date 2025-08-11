package com.aiphone.controller;

import com.aiphone.common.Result;
import com.aiphone.dto.BannerDTO;
import com.aiphone.service.BannerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 横幅控制器
 */
@Api(tags = "横幅管理")
@RestController
@RequestMapping("/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取首页横幅列表
     */
    @ApiOperation("获取首页横幅列表")
    @GetMapping("/home")
    public Result<List<BannerDTO>> getHomeBanners(
            @ApiParam("数量限制") @RequestParam(defaultValue = "5") Integer limit) {
        
        try {
            List<BannerDTO> result = bannerService.getHomeBanners(limit);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取首页横幅失败：" + e.getMessage());
        }
    }

    /**
     * 根据类型获取横幅列表
     */
    @ApiOperation("根据类型获取横幅列表")
    @GetMapping("/type/{type}")
    public Result<List<BannerDTO>> getBannersByType(@ApiParam("横幅类型") @PathVariable String type) {
        try {
            List<BannerDTO> result = bannerService.getBannersByType(type);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取横幅列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有有效的横幅列表
     */
    @ApiOperation("获取所有有效的横幅列表")
    @GetMapping("/active")
    public Result<List<BannerDTO>> getAllActiveBanners() {
        try {
            List<BannerDTO> result = bannerService.getAllActiveBanners();
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取有效横幅失败：" + e.getMessage());
        }
    }

    /**
     * 获取横幅列表（分页）
     */
    @ApiOperation("获取横幅列表")
    @GetMapping
    public Result<IPage<BannerDTO>> getBannerList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("横幅类型") @RequestParam(required = false) String type,
            @ApiParam("状态：1-启用，0-禁用") @RequestParam(required = false) Integer status) {
        
        try {
            IPage<BannerDTO> result = bannerService.getBannerList(page, pageSize, type, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取横幅列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取横幅详情
     */
    @ApiOperation("获取横幅详情")
    @GetMapping("/{id}")
    public Result<BannerDTO> getBannerDetail(@ApiParam("横幅ID") @PathVariable Long id) {
        try {
            BannerDTO banner = bannerService.getBannerDetail(id);
            if (banner == null) {
                return Result.error("横幅不存在");
            }
            return Result.success(banner);
        } catch (Exception e) {
            return Result.error("获取横幅详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建横幅
     */
    @ApiOperation("创建横幅")
    @PostMapping
    public Result<Boolean> createBanner(@RequestBody BannerDTO bannerDTO) {
        try {
            boolean result = bannerService.createBanner(bannerDTO);
            if (result) {
                return Result.success(true, "创建横幅成功");
            } else {
                return Result.error("创建横幅失败");
            }
        } catch (Exception e) {
            return Result.error("创建横幅失败：" + e.getMessage());
        }
    }

    /**
     * 更新横幅信息
     */
    @ApiOperation("更新横幅信息")
    @PutMapping("/{id}")
    public Result<Boolean> updateBanner(
            @ApiParam("横幅ID") @PathVariable Long id,
            @RequestBody BannerDTO bannerDTO) {
        try {
            boolean result = bannerService.updateBanner(id, bannerDTO);
            if (result) {
                return Result.success(true, "更新横幅成功");
            } else {
                return Result.error("更新横幅失败");
            }
        } catch (Exception e) {
            return Result.error("更新横幅失败：" + e.getMessage());
        }
    }

    /**
     * 删除横幅
     */
    @ApiOperation("删除横幅")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteBanner(@ApiParam("横幅ID") @PathVariable Long id) {
        try {
            boolean result = bannerService.deleteBanner(id);
            if (result) {
                return Result.success(true, "删除横幅成功");
            } else {
                return Result.error("删除横幅失败");
            }
        } catch (Exception e) {
            return Result.error("删除横幅失败：" + e.getMessage());
        }
    }

    /**
     * 启用横幅
     */
    @ApiOperation("启用横幅")
    @PutMapping("/{id}/enable")
    public Result<Boolean> enableBanner(@ApiParam("横幅ID") @PathVariable Long id) {
        try {
            boolean result = bannerService.enableBanner(id);
            if (result) {
                return Result.success(true, "启用横幅成功");
            } else {
                return Result.error("启用横幅失败");
            }
        } catch (Exception e) {
            return Result.error("启用横幅失败：" + e.getMessage());
        }
    }

    /**
     * 禁用横幅
     */
    @ApiOperation("禁用横幅")
    @PutMapping("/{id}/disable")
    public Result<Boolean> disableBanner(@ApiParam("横幅ID") @PathVariable Long id) {
        try {
            boolean result = bannerService.disableBanner(id);
            if (result) {
                return Result.success(true, "禁用横幅成功");
            } else {
                return Result.error("禁用横幅失败");
            }
        } catch (Exception e) {
            return Result.error("禁用横幅失败：" + e.getMessage());
        }
    }
} 