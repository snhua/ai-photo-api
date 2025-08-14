package com.aiphone.controller;

import com.aiphone.dto.OrderDTO;
import com.aiphone.service.OrderService;
import com.aiphone.util.UserContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取订单列表
     */
    @ApiOperation("获取订单列表")
    @GetMapping
    public Map<String, Object> getOrderList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("订单状态") @RequestParam(required = false) String status,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("绘画师ID") @RequestParam(required = false) Long artistId) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<OrderDTO> data = orderService.getOrderList(page, pageSize, status, userId, artistId);
            result.put("code", 0);
            result.put("message", "success");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "获取订单列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据用户ID获取订单列表
     */
    @ApiOperation("根据用户ID获取订单列表")
    @GetMapping("/user/{userId}")
    public Map<String, Object> getOrdersByUserId(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("订单状态") @RequestParam(required = false) String status) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<OrderDTO> data = orderService.getOrdersByUserId(userId, page, pageSize, status);
            result.put("code", 0);
            result.put("message", "success");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "获取用户订单列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据绘画师ID获取订单列表
     */
    @ApiOperation("根据绘画师ID获取订单列表")
    @GetMapping("/artist/{artistId}")
    public Map<String, Object> getOrdersByArtistId(
            @ApiParam("绘画师ID") @PathVariable Long artistId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("订单状态") @RequestParam(required = false) String status) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<OrderDTO> data = orderService.getOrdersByArtistId(artistId, page, pageSize, status);
            result.put("code", 0);
            result.put("message", "success");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "获取绘画师订单列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取订单详情
     */
    @ApiOperation("获取订单详情")
    @GetMapping("/{id}")
    public Map<String, Object> getOrderDetail(@ApiParam("订单ID") @PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            OrderDTO order = orderService.getOrderDetail(id);
            if (order == null) {
                result.put("code", -1);
                result.put("message", "订单不存在");
            } else {
                result.put("code", 0);
                result.put("message", "success");
                result.put("data", order);
            }
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "获取订单详情失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据订单号获取订单详情
     */
    @ApiOperation("根据订单号获取订单详情")
    @GetMapping("/no/{orderNo}")
    public Map<String, Object> getOrderByOrderNo(@ApiParam("订单号") @PathVariable String orderNo) {
        Map<String, Object> result = new HashMap<>();
        try {
            OrderDTO order = orderService.getOrderByOrderNo(orderNo);
            if (order == null) {
                result.put("code", -1);
                result.put("message", "订单不存在");
            } else {
                result.put("code", 0);
                result.put("message", "success");
                result.put("data", order);
            }
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "获取订单详情失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 创建订单
     */
    @ApiOperation("创建订单")
    @PostMapping
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 创建OrderDTO对象
            OrderDTO orderDTO = new OrderDTO();
            
            // 设置基本信息
            orderDTO.setTitle((String) request.get("title"));
            orderDTO.setDescription((String) request.get("description"));
            
            // 处理价格，支持字符串和数字格式
            Object priceObj = request.get("price");
            if (priceObj != null) {
                if (priceObj instanceof String) {
                    orderDTO.setPrice(new BigDecimal((String) priceObj));
                } else if (priceObj instanceof Number) {
                    orderDTO.setPrice(new BigDecimal(priceObj.toString()));
                }
            }
            
            // 从当前登录用户获取用户ID
            Long currentUserId = UserContext.getCurrentUserId();
            orderDTO.setUserId(currentUserId);
            
            // 处理绘画师ID，支持用户指定或使用默认值
            Object artistIdObj = request.get("artistId");
            if (artistIdObj != null) {
                if (artistIdObj instanceof String) {
                    orderDTO.setArtistId(Long.parseLong((String) artistIdObj));
                } else if (artistIdObj instanceof Number) {
                    orderDTO.setArtistId(((Number) artistIdObj).longValue());
                }
            } else {
                // 如果没有指定绘画师ID，使用默认值
                orderDTO.setArtistId(0L);
            }
            
            // 设置默认状态
            orderDTO.setStatus("pending");
            
            // 处理可选参数
            if (request.get("referenceImages") != null) {
                if (request.get("referenceImages") instanceof String) {
                    // 如果是字符串，转换为List
                    List<String> images = new ArrayList<>();
                    images.add((String) request.get("referenceImages"));
                    orderDTO.setReferenceImages(images);
                } else if (request.get("referenceImages") instanceof List) {
                    orderDTO.setReferenceImages((List<String>) request.get("referenceImages"));
                }
            }
            
            if (request.get("requirements") != null) {
                orderDTO.setRequirements((String) request.get("requirements"));
            }
            
            if (request.get("deadline") != null) {
                orderDTO.setDeadline(LocalDateTime.parse((String) request.get("deadline")));
            }
            
            // 创建订单
            Long orderId = orderService.createOrder(orderDTO);
            if (orderId != null) {
                // 获取创建的订单详情
                OrderDTO createdOrder = orderService.getOrderDetail(orderId);
                result.put("code", 0);
                result.put("message", "创建订单成功");
                result.put("data", createdOrder);
            } else {
                result.put("code", -1);
                result.put("message", "创建订单失败");
            }
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "创建订单失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新订单信息
     */
    @ApiOperation("更新订单信息")
    @PutMapping("/{id}")
    public Map<String, Object> updateOrder(
            @ApiParam("订单ID") @PathVariable Long id,
            @RequestBody OrderDTO orderDTO) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = orderService.updateOrder(id, orderDTO);
            result.put("code", success ? 0 : -1);
            result.put("message", success ? "更新订单成功" : "更新订单失败");
            result.put("data", success);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "更新订单失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除订单
     */
    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteOrder(@ApiParam("订单ID") @PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = orderService.deleteOrder(id);
            result.put("code", success ? 0 : -1);
            result.put("message", success ? "删除订单成功" : "删除订单失败");
            result.put("data", success);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "删除订单失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新订单状态
     */
    @ApiOperation("更新订单状态")
    @PutMapping("/{id}/status")
    public Map<String, Object> updateOrderStatus(
            @ApiParam("订单ID") @PathVariable Long id,
            @ApiParam("新状态") @RequestParam String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = orderService.updateOrderStatus(id, status);
            result.put("code", success ? 0 : -1);
            result.put("message", success ? "更新订单状态成功" : "更新订单状态失败");
            result.put("data", success);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "更新订单状态失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 接受订单
     */
    @ApiOperation("接受订单")
    @PutMapping("/{id}/accept")
    public Map<String, Object> acceptOrder(
            @ApiParam("订单ID") @PathVariable Long id,
            @ApiParam("绘画师ID") @RequestParam Long artistId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = orderService.acceptOrder(id, artistId);
            result.put("code", success ? 0 : -1);
            result.put("message", success ? "接受订单成功" : "接受订单失败");
            result.put("data", success);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "接受订单失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 开始制作订单
     */
    @ApiOperation("开始制作订单")
    @PutMapping("/{id}/start")
    public Map<String, Object> startOrder(@ApiParam("订单ID") @PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = orderService.startOrder(id);
            result.put("code", success ? 0 : -1);
            result.put("message", success ? "开始制作订单成功" : "开始制作订单失败");
            result.put("data", success);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "开始制作订单失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 完成订单
     */
    @ApiOperation("完成订单")
    @PutMapping("/{id}/complete")
    public Map<String, Object> completeOrder(@ApiParam("订单ID") @PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = orderService.completeOrder(id);
            result.put("code", success ? 0 : -1);
            result.put("message", success ? "完成订单成功" : "完成订单失败");
            result.put("data", success);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "完成订单失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 取消订单
     */
    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    public Map<String, Object> cancelOrder(@ApiParam("订单ID") @PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = orderService.cancelOrder(id);
            result.put("code", success ? 0 : -1);
            result.put("message", success ? "取消订单成功" : "取消订单失败");
            result.put("data", success);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "取消订单失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 确认收货
     */
    @ApiOperation("确认收货")
    @PutMapping("/{id}/confirm")
    public Map<String, Object> confirmOrder(@ApiParam("订单ID") @PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = orderService.confirmOrder(id);
            result.put("code", success ? 0 : -1);
            result.put("message", success ? "确认收货成功" : "确认收货失败");
            result.put("data", success);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "确认收货失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取订单统计信息
     */
    @ApiOperation("获取订单统计信息")
    @GetMapping("/statistics")
    public Map<String, Object> getOrderStatistics(
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("绘画师ID") @RequestParam(required = false) Long artistId) {
        Map<String, Object> result = new HashMap<>();
        try {
            OrderService.OrderStatistics statistics = orderService.getOrderStatistics(userId, artistId);
            result.put("code", 0);
            result.put("message", "success");
            result.put("data", statistics);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("message", "获取订单统计信息失败：" + e.getMessage());
        }
        return result;
    }
} 