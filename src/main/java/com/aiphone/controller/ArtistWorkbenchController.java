package com.aiphone.controller;

import com.aiphone.common.Result;
import com.aiphone.dto.OrderDTO;
import com.aiphone.dto.ArtworkDTO;
import com.aiphone.dto.DeliveryDTO;
import com.aiphone.service.OrderService;
import com.aiphone.service.ArtworkService;
import com.aiphone.service.ArtistService;
import com.aiphone.security.SecurityUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

/**
 * 画师工作台控制器
 */
@Slf4j
@RestController
@RequestMapping("/artist/workbench")
@Api(tags = "画师工作台")
public class ArtistWorkbenchController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ArtworkService artworkService;

    @Autowired
    private ArtistService artistService;

    /**
     * 获取可接订单列表
     */
    @ApiOperation("获取可接订单列表")
    @GetMapping("/available-orders")
    public Result<IPage<OrderDTO>> getAvailableOrders(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("分类筛选") @RequestParam(required = false) String category,
            @ApiParam("价格范围") @RequestParam(required = false) String priceRange) {
        
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            IPage<OrderDTO> orders = orderService.getAvailableOrders(artistId, page, pageSize, category, priceRange);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("获取可接订单列表失败", e);
            return Result.error(1005, "获取可接订单列表失败：" + e.getMessage());
        }
    }

    /**
     * 接单
     */
    @ApiOperation("接单")
    @PostMapping("/orders/{orderId}/accept")
    public Result<Boolean> acceptOrder(@ApiParam("订单ID") @PathVariable Long orderId) {
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            boolean success = orderService.acceptOrder(orderId, artistId);
            if (success) {
                return Result.success(true, "接单成功");
            } else {
                return Result.error(1004, "接单失败，订单可能已被其他画师接取");
            }
        } catch (Exception e) {
            log.error("接单失败", e);
            return Result.error(1005, "接单失败：" + e.getMessage());
        }
    }

    /**
     * 获取我的订单列表
     */
    @ApiOperation("获取我的订单列表")
    @GetMapping("/my-orders")
    public Result<IPage<OrderDTO>> getMyOrders(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("订单状态") @RequestParam(required = false) String status) {
        
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            IPage<OrderDTO> orders = orderService.getOrdersByArtistId(artistId, page, pageSize, status);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("获取我的订单列表失败", e);
            return Result.error(1005, "获取我的订单列表失败：" + e.getMessage());
        }
    }

    /**
     * 开始制作订单
     */
    @ApiOperation("开始制作订单")
    @PostMapping("/orders/{orderId}/start")
    public Result<Boolean> startOrder(@ApiParam("订单ID") @PathVariable Long orderId) {
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            // 验证订单是否属于当前画师
            OrderDTO order = orderService.getOrderDetail(orderId);
            if (order == null || !artistId.equals(order.getArtistId())) {
                return Result.error(1004, "订单不存在或不属于您");
            }
            
            boolean success = orderService.startOrder(orderId);
            if (success) {
                return Result.success(true, "开始制作订单成功");
            } else {
                return Result.error(1004, "开始制作订单失败");
            }
        } catch (Exception e) {
            log.error("开始制作订单失败", e);
            return Result.error(1005, "开始制作订单失败：" + e.getMessage());
        }
    }

    /**
     * 上传作品草稿
     */
    @ApiOperation("上传作品草稿")
    @PostMapping("/orders/{orderId}/draft")
    public Result<String> uploadDraft(
            @ApiParam("订单ID") @PathVariable Long orderId,
            @ApiParam("作品文件") @RequestParam("file") MultipartFile file,
            @ApiParam("草稿说明") @RequestParam(required = false) String description) {
        
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            // 验证订单是否属于当前画师
            OrderDTO order = orderService.getOrderDetail(orderId);
            if (order == null || !artistId.equals(order.getArtistId())) {
                return Result.error(1004, "订单不存在或不属于您");
            }
            
            String fileUrl = artworkService.uploadDraft(orderId, file, description);
            return Result.success(fileUrl);
        } catch (Exception e) {
            log.error("上传草稿失败", e);
            return Result.error(1005, "上传草稿失败：" + e.getMessage());
        }
    }

    /**
     * 提交最终作品
     */
    @ApiOperation("提交最终作品")
    @PostMapping("/orders/{orderId}/deliver")
    public Result<Boolean> deliverOrder(
            @ApiParam("订单ID") @PathVariable Long orderId,
            @Valid @RequestBody DeliveryDTO deliveryDTO) {
        
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            // 验证订单是否属于当前画师
            OrderDTO order = orderService.getOrderDetail(orderId);
            if (order == null || !artistId.equals(order.getArtistId())) {
                return Result.error(1004, "订单不存在或不属于您");
            }
            
            boolean success = orderService.deliverOrder(orderId, deliveryDTO);
            if (success) {
                return Result.success(true, "提交作品成功");
            } else {
                return Result.error(1004, "提交作品失败");
            }
        } catch (Exception e) {
            log.error("提交作品失败", e);
            return Result.error(1005, "提交作品失败：" + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @ApiOperation("获取订单详情")
    @GetMapping("/orders/{orderId}")
    public Result<OrderDTO> getOrderDetail(@ApiParam("订单ID") @PathVariable Long orderId) {
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            OrderDTO order = orderService.getOrderDetail(orderId);
            if (order == null) {
                return Result.error(1004, "订单不存在");
            }
            
            // 验证订单是否属于当前画师
//            if (!artistId.equals(order.getArtistId())) {
//                return Result.error(1003, "无权限查看此订单");
//            }
            
            return Result.success(order);
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return Result.error(1005, "获取订单详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取我的作品列表
     */
    @ApiOperation("获取我的作品列表")
    @GetMapping("/my-works")
    public Result<IPage<ArtworkDTO>> getMyWorks(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("分类筛选") @RequestParam(required = false) String category) {
        
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            IPage<ArtworkDTO> works = artworkService.getWorksByArtistId(artistId, page, pageSize, category);
            return Result.success(works);
        } catch (Exception e) {
            log.error("获取我的作品列表失败", e);
            return Result.error(1005, "获取我的作品列表失败：" + e.getMessage());
        }
    }

    /**
     * 上传作品到作品集
     */
    @ApiOperation("上传作品到作品集")
    @PostMapping("/works")
    public Result<Long> uploadWork(
            @ApiParam("作品文件") @RequestParam("file") MultipartFile file,
            @Valid @RequestBody ArtworkDTO artworkDTO) {
        
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            artworkDTO.setArtistId(artistId);
            
            Long workId = artworkService.uploadWork(file, artworkDTO);
            return Result.success(workId);
        } catch (Exception e) {
            log.error("上传作品失败", e);
            return Result.error(1005, "上传作品失败：" + e.getMessage());
        }
    }

    /**
     * 更新作品信息
     */
    @ApiOperation("更新作品信息")
    @PutMapping("/works/{workId}")
    public Result<Boolean> updateWork(
            @ApiParam("作品ID") @PathVariable Long workId,
            @Valid @RequestBody ArtworkDTO artworkDTO) {
        
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            // 验证作品是否属于当前画师
            ArtworkDTO work = artworkService.getArtworkDetail(workId);
            if (work == null || !artistId.equals(work.getArtistId())) {
                return Result.error(1004, "作品不存在或不属于您");
            }
            
            boolean success = artworkService.updateArtwork(workId, artworkDTO);
            if (success) {
                return Result.success(true, "更新作品成功");
            } else {
                return Result.error(1004, "更新作品失败");
            }
        } catch (Exception e) {
            log.error("更新作品失败", e);
            return Result.error(1005, "更新作品失败：" + e.getMessage());
        }
    }

    /**
     * 删除作品
     */
    @ApiOperation("删除作品")
    @DeleteMapping("/works/{workId}")
    public Result<Boolean> deleteWork(@ApiParam("作品ID") @PathVariable Long workId) {
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            // 验证作品是否属于当前画师
            ArtworkDTO work = artworkService.getArtworkDetail(workId);
            if (work == null || !artistId.equals(work.getArtistId())) {
                return Result.error(1004, "作品不存在或不属于您");
            }
            
            boolean success = artworkService.deleteArtwork(workId);
            if (success) {
                return Result.success(true, "删除作品成功");
            } else {
                return Result.error(1004, "删除作品失败");
            }
        } catch (Exception e) {
            log.error("删除作品失败", e);
            return Result.error(1005, "删除作品失败：" + e.getMessage());
        }
    }

    /**
     * 获取工作统计信息
     */
    @ApiOperation("获取工作统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getWorkStatistics() {
        try {
            Long artistId = SecurityUtils.getCurrentUserId();
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取订单统计
            OrderService.OrderStatistics orderStats = orderService.getOrderStatistics(null, artistId);
            statistics.put("orderStats", orderStats);
            
            // 获取作品统计
            long totalWorks = artworkService.getWorksCountByArtistId(artistId);
            statistics.put("totalWorks", totalWorks);
            
            // 获取收入统计
            BigDecimal totalIncome = orderService.getArtistIncome(artistId);
            statistics.put("totalIncome", totalIncome);
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取工作统计信息失败", e);
            return Result.error(1005, "获取工作统计信息失败：" + e.getMessage());
        }
    }
} 