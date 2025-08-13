package com.aiphone.service.impl;

import com.aiphone.dto.ArtistDTO;
import com.aiphone.dto.OrderDTO;
import com.aiphone.dto.UserDTO;
import com.aiphone.dto.DeliveryDTO;
import com.aiphone.entity.Order;
import com.aiphone.mapper.OrderMapper;
import com.aiphone.service.ArtistService;
import com.aiphone.service.OrderService;
import com.aiphone.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.aiphone.entity.User;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Long getOrderIdByOrderNo(String orderNo) {
        // 根据订单号查询订单ID
        Order order = orderMapper.getByOrderNo(orderNo);
        return order != null ? order.getId() : null;
    }

    @Override
    public boolean updateOrderStatus(String orderNo, String status) {
        // 根据订单号更新订单状态
        Order order = orderMapper.getByOrderNo(orderNo);
        if (order != null) {
            order.setStatus(status);
            return this.updateById(order);
        }
        return false;
    }

    @Override
    public IPage<OrderDTO> getOrderList(Integer page, Integer pageSize, String status, Long userId, Long artistId) {
        Page<Order> pageParam = new Page<>(page, pageSize);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (artistId != null) {
            queryWrapper.eq("artist_id", artistId);
        }
        
        queryWrapper.orderByDesc("created_at");
        
        IPage<Order> orderPage = this.page(pageParam, queryWrapper);
        return convertToOrderDTOPage(orderPage);
    }

    @Override
    public IPage<OrderDTO> getAvailableOrders(Long artistId, Integer page, Integer pageSize, String category, String priceRange) {
        Page<Order> pageParam = new Page<>(page, pageSize);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        // 只查询待接单的订单
        queryWrapper.eq("status", "pending");
        
        // 排除已经被接的订单
        queryWrapper.isNull("artist_id").or().eq("artist_id", 0);
        
        // 分类筛选
        if (StringUtils.hasText(category)) {
            queryWrapper.eq("category", category);
        }
        
        // 价格范围筛选
        if (StringUtils.hasText(priceRange)) {
            String[] range = priceRange.split("-");
            if (range.length == 2) {
                try {
                    BigDecimal minPrice = new BigDecimal(range[0]);
                    BigDecimal maxPrice = new BigDecimal(range[1]);
                    queryWrapper.between("price", minPrice, maxPrice);
                } catch (NumberFormatException e) {
                    // 价格格式错误，忽略价格筛选
                }
            }
        }
        
        queryWrapper.orderByDesc("created_at");
        
        IPage<Order> orderPage = this.page(pageParam, queryWrapper);
        return convertToOrderDTOPage(orderPage);
    }

    @Override
    public OrderDTO getOrderDetail(Long id) {
        Order order = this.getById(id);
        if (order == null) {
            return null;
        }
        return convertToOrderDTO(order);
    }

    @Override
    public OrderDTO getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.getByOrderNo(orderNo);
        if (order == null) {
            return null;
        }
        return convertToOrderDTO(order);
    }

    @Override
    public IPage<OrderDTO> getOrdersByUserId(Long userId, Integer page, Integer pageSize, String status) {
        Page<Order> pageParam = new Page<>(page, pageSize);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("created_at");
        
        IPage<Order> orderPage = this.page(pageParam, queryWrapper);
        return convertToOrderDTOPage(orderPage);
    }

    @Override
    public IPage<OrderDTO> getOrdersByArtistId(Long artistId, Integer page, Integer pageSize, String status) {
        Page<Order> pageParam = new Page<>(page, pageSize);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("artist_id", artistId);
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("created_at");
        
        IPage<Order> orderPage = this.page(pageParam, queryWrapper);
        return convertToOrderDTOPage(orderPage);
    }

    @Override
    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        
        // 生成订单号
        order.setOrderNo(generateOrderNo());
        
        // 设置默认状态
        if (!StringUtils.hasText(order.getStatus())) {
            order.setStatus("pending");
        }
        
        // 转换参考图片为JSON字符串
        if (orderDTO.getReferenceImages() != null && !orderDTO.getReferenceImages().isEmpty()) {
            try {
                order.setReferenceImages(objectMapper.writeValueAsString(orderDTO.getReferenceImages()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换参考图片失败", e);
            }
        }
        
        boolean success = this.save(order);
        if (success) {
            return order.getId();
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public boolean updateOrder(Long id, OrderDTO orderDTO) {
        Order order = this.getById(id);
        if (order == null) {
            return false;
        }
        
        BeanUtils.copyProperties(orderDTO, order);
        order.setId(id);
        
        // 转换参考图片为JSON字符串
        if (orderDTO.getReferenceImages() != null && !orderDTO.getReferenceImages().isEmpty()) {
            try {
                order.setReferenceImages(objectMapper.writeValueAsString(orderDTO.getReferenceImages()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换参考图片失败", e);
            }
        }
        
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean deleteOrder(Long id) {
        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Long id, String status) {
        Order order = this.getById(id);
        if (order == null) {
            return false;
        }
        
        order.setStatus(status);
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean acceptOrder(Long id, Long artistId) {
        Order order = this.getById(id);
        if (order == null) {
            return false;
        }
        
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法接受");
        }
        
        order.setArtistId(artistId);
        order.setStatus("accepted");
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean startOrder(Long id) {
        Order order = this.getById(id);
        if (order == null) {
            return false;
        }
        
        if (!"accepted".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法开始制作");
        }
        
        order.setStatus("in_progress");
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean completeOrder(Long id) {
        Order order = this.getById(id);
        if (order == null) {
            return false;
        }
        
        if (!"in_progress".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法完成");
        }
        
        order.setStatus("completed");
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long id) {
        Order order = this.getById(id);
        if (order == null) {
            return false;
        }
        
        if ("completed".equals(order.getStatus()) || "cancelled".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法取消");
        }
        
        order.setStatus("cancelled");
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean deliverOrder(Long orderId, DeliveryDTO deliveryDTO) {
        try {
            Order order = this.getById(orderId);
            if (order == null) {
                return false;
            }
            
            // 更新订单状态为已完成
            order.setStatus("completed");
            
            // 保存作品信息到订单（暂时注释掉，因为Order实体没有这些字段）
            // if (deliveryDTO.getArtworkUrls() != null && !deliveryDTO.getArtworkUrls().isEmpty()) {
            //     order.setArtworkUrls(JSON.toJSONString(deliveryDTO.getArtworkUrls()));
            // }
            
            // 保存作品说明（暂时注释掉，因为Order实体没有这些字段）
            // if (StringUtils.hasText(deliveryDTO.getNotes())) {
            //     order.setNotes(deliveryDTO.getNotes());
            // }
            
            // 保存技术说明（暂时注释掉，因为Order实体没有这些字段）
            // if (StringUtils.hasText(deliveryDTO.getTechnicalNotes())) {
            //     order.setTechnicalNotes(deliveryDTO.getTechnicalNotes());
            // }
            
            // 保存制作时间（暂时注释掉，因为Order实体没有这些字段）
            // if (deliveryDTO.getWorkHours() != null) {
            //     order.setWorkHours(deliveryDTO.getWorkHours());
            // }
            
            return this.updateById(order);
        } catch (Exception e) {
            throw new RuntimeException("提交作品失败", e);
        }
    }

    @Override
    public BigDecimal getArtistIncome(Long artistId) {
        try {
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("artist_id", artistId)
                       .eq("status", "completed");
            
            List<Order> completedOrders = this.list(queryWrapper);
            
            return completedOrders.stream()
                    .map(Order::getPrice)
                    .filter(price -> price != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            throw new RuntimeException("获取画师收入失败", e);
        }
    }

    @Override
    public OrderStatistics getOrderStatistics(Long userId, Long artistId) {
        OrderStatistics statistics = new OrderStatistics();
        
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (artistId != null) {
            queryWrapper.eq("artist_id", artistId);
        }
        
        // 总订单数
        statistics.setTotal(this.count(queryWrapper));
        
        // 各状态订单数
        QueryWrapper<Order> pendingWrapper = new QueryWrapper<>();
        if (userId != null) {
            pendingWrapper.eq("user_id", userId);
        }
        if (artistId != null) {
            pendingWrapper.eq("artist_id", artistId);
        }
        pendingWrapper.eq("status", "pending");
        statistics.setPending(this.count(pendingWrapper));
        
        QueryWrapper<Order> acceptedWrapper = new QueryWrapper<>();
        if (userId != null) {
            acceptedWrapper.eq("user_id", userId);
        }
        if (artistId != null) {
            acceptedWrapper.eq("artist_id", artistId);
        }
        acceptedWrapper.eq("status", "accepted");
        statistics.setAccepted(this.count(acceptedWrapper));
        
        QueryWrapper<Order> inProgressWrapper = new QueryWrapper<>();
        if (userId != null) {
            inProgressWrapper.eq("user_id", userId);
        }
        if (artistId != null) {
            inProgressWrapper.eq("artist_id", artistId);
        }
        inProgressWrapper.eq("status", "in_progress");
        statistics.setInProgress(this.count(inProgressWrapper));
        
        QueryWrapper<Order> completedWrapper = new QueryWrapper<>();
        if (userId != null) {
            completedWrapper.eq("user_id", userId);
        }
        if (artistId != null) {
            completedWrapper.eq("artist_id", artistId);
        }
        completedWrapper.eq("status", "completed");
        statistics.setCompleted(this.count(completedWrapper));
        
        QueryWrapper<Order> cancelledWrapper = new QueryWrapper<>();
        if (userId != null) {
            cancelledWrapper.eq("user_id", userId);
        }
        if (artistId != null) {
            cancelledWrapper.eq("artist_id", artistId);
        }
        cancelledWrapper.eq("status", "cancelled");
        statistics.setCancelled(this.count(cancelledWrapper));
        
        return statistics;
    }

    // 使用Gson将JSONArray转换为List
    public static <T> List<T> jsonArrayToListWithGson(JSONArray jsonArray, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        jsonArray.forEach(e->{
            list.add((T)JSON.toJSONString(e));
        });
        return list;
    }

    /**
     * 将Order实体转换为OrderDTO
     */
    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        
        // 转换参考图片JSON字符串为List
        if (StringUtils.hasText(order.getReferenceImages())) {
            try {

                JSONArray fastJsonArray = JSONArray.parseArray( order.getReferenceImages());
                List<String> fastJsonList = jsonArrayToListWithGson(fastJsonArray, String.class);


//                List<String> referenceImages = objectMapper.readValue(
//                    order.getReferenceImages(),
//                    new TypeReference<List<String>>() {}
//                );
                orderDTO.setReferenceImages(fastJsonList);
            } catch (Exception e) {
                orderDTO.setReferenceImages(new ArrayList<>());
            }
        }
        
        // 获取用户信息
        if (order.getUserId() != null) {
            User user = userService.getUserById(order.getUserId());
            if (user != null) {
                UserDTO userDTO = new UserDTO();
                BeanUtils.copyProperties(user, userDTO);
                orderDTO.setUser(userDTO);
            }
        }
        
        // 获取绘画师信息
        if (order.getArtistId() != null) {
            ArtistDTO artist = artistService.getArtistDetail(order.getArtistId());
            orderDTO.setArtist(artist);
        }
        
        return orderDTO;
    }

    /**
     * 将Order分页结果转换为OrderDTO分页结果
     */
    private IPage<OrderDTO> convertToOrderDTOPage(IPage<Order> orderPage) {
        Page<OrderDTO> orderDTOPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        
        List<OrderDTO> orderDTOList = orderPage.getRecords().stream()
            .map(this::convertToOrderDTO)
            .collect(Collectors.toList());
        
        orderDTOPage.setRecords(orderDTOList);
        return orderDTOPage;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8);
        return "ORDER" + timestamp + random;
    }
} 