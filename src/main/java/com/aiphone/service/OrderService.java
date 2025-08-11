package com.aiphone.service;

import com.aiphone.dto.OrderDTO;
import com.aiphone.entity.Order;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 根据订单号获取订单ID
     *
     * @param orderNo 订单号
     * @return 订单ID，如果不存在返回null
     */
    Long getOrderIdByOrderNo(String orderNo);

    /**
     * 更新订单状态
     *
     * @param orderNo 订单号
     * @param status 订单状态
     * @return 是否更新成功
     */
    boolean updateOrderStatus(String orderNo, String status);

    /**
     * 获取订单列表（分页）
     *
     * @param page 页码
     * @param pageSize 每页数量
     * @param status 订单状态
     * @param userId 用户ID
     * @param artistId 绘画师ID
     * @return 订单列表
     */
    IPage<OrderDTO> getOrderList(Integer page, Integer pageSize, String status, Long userId, Long artistId);

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    OrderDTO getOrderDetail(Long id);

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo 订单号
     * @return 订单详情
     */
    OrderDTO getOrderByOrderNo(String orderNo);

    /**
     * 根据用户ID获取订单列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param status 订单状态
     * @return 订单列表
     */
    IPage<OrderDTO> getOrdersByUserId(Long userId, Integer page, Integer pageSize, String status);

    /**
     * 根据绘画师ID获取订单列表
     *
     * @param artistId 绘画师ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param status 订单状态
     * @return 订单列表
     */
    IPage<OrderDTO> getOrdersByArtistId(Long artistId, Integer page, Integer pageSize, String status);

    /**
     * 创建订单
     *
     * @param orderDTO 订单信息
     * @return 创建结果
     */
    Long createOrder(OrderDTO orderDTO);

    /**
     * 更新订单信息
     *
     * @param id 订单ID
     * @param orderDTO 订单信息
     * @return 更新结果
     */
    boolean updateOrder(Long id, OrderDTO orderDTO);

    /**
     * 删除订单
     *
     * @param id 订单ID
     * @return 删除结果
     */
    boolean deleteOrder(Long id);

    /**
     * 更新订单状态
     *
     * @param id 订单ID
     * @param status 新状态
     * @return 更新结果
     */
    boolean updateOrderStatus(Long id, String status);

    /**
     * 接受订单
     *
     * @param id 订单ID
     * @param artistId 绘画师ID
     * @return 操作结果
     */
    boolean acceptOrder(Long id, Long artistId);

    /**
     * 开始制作订单
     *
     * @param id 订单ID
     * @return 操作结果
     */
    boolean startOrder(Long id);

    /**
     * 完成订单
     *
     * @param id 订单ID
     * @return 操作结果
     */
    boolean completeOrder(Long id);

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 操作结果
     */
    boolean cancelOrder(Long id);

    /**
     * 获取订单统计信息
     *
     * @param userId 用户ID（可选）
     * @param artistId 绘画师ID（可选）
     * @return 统计信息
     */
    OrderStatistics getOrderStatistics(Long userId, Long artistId);

    /**
     * 订单统计信息
     */
    class OrderStatistics {
        private Long total;
        private Long pending;
        private Long accepted;
        private Long inProgress;
        private Long completed;
        private Long cancelled;

        // Getters and Setters
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }

        public Long getPending() { return pending; }
        public void setPending(Long pending) { this.pending = pending; }

        public Long getAccepted() { return accepted; }
        public void setAccepted(Long accepted) { this.accepted = accepted; }

        public Long getInProgress() { return inProgress; }
        public void setInProgress(Long inProgress) { this.inProgress = inProgress; }

        public Long getCompleted() { return completed; }
        public void setCompleted(Long completed) { this.completed = completed; }

        public Long getCancelled() { return cancelled; }
        public void setCancelled(Long cancelled) { this.cancelled = cancelled; }
    }
} 