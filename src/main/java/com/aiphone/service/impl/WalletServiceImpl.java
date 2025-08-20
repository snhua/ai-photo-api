package com.aiphone.service.impl;

import com.aiphone.common.Result;
import com.aiphone.dto.WalletInfo;
import com.aiphone.dto.WalletTransactionDTO;
import com.aiphone.dto.WithdrawalRequestDTO;

import com.aiphone.entity.*;
import com.aiphone.mapper.*;
import com.aiphone.service.WalletService;
import com.aiphone.service.OrderService;
import com.aiphone.service.SystemConfigService;
import com.aiphone.service.PaymentService;
import com.aiphone.service.WechatPayService;
import com.aiphone.dto.PaymentRequest;
import com.aiphone.dto.PaymentResponse;
import com.aiphone.service.out.WeChatEnterprisePayService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * 钱包服务实现类
 */
@Slf4j
@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WalletTransactionMapper walletTransactionMapper;

    @Autowired
    private WalletPendingWithdrawalMapper walletPendingWithdrawalMapper;

    @Autowired
    private WithdrawalRequestMapper withdrawalRequestMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    @Lazy
    private PaymentService paymentService;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    WeChatEnterprisePayService weChatEnterprisePayService;
    private final String return_msg = "return_msg";

    @Override
    public WalletInfo getWalletInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        WalletInfo walletInfo = new WalletInfo();
        walletInfo.setUserId(userId);
        walletInfo.setBalance(user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO);
        walletInfo.setWithdrawableBalance(user.getWithdrawableBalance() != null ? user.getWithdrawableBalance() : BigDecimal.ZERO);
        walletInfo.setTotalIncome(user.getTotalIncome() != null ? user.getTotalIncome() : BigDecimal.ZERO);
        walletInfo.setTotalWithdraw(user.getTotalWithdraw() != null ? user.getTotalWithdraw() : BigDecimal.ZERO);
        walletInfo.setServiceFeeTotal(user.getServiceFeeTotal() != null ? user.getServiceFeeTotal() : BigDecimal.ZERO);

        // 获取最近交易记录
        List<WalletTransaction> recentTransactions = walletTransactionMapper.getByUserId(userId);
        List<WalletTransactionDTO> transactionDTOs = recentTransactions.stream()
                .limit(10)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        walletInfo.setRecentTransactions(transactionDTOs);

        return walletInfo;
    }


    @Override
    @Transactional
    public Result<WithdrawalRequest> withdraw(Long userId, WithdrawalRequestDTO request) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error(1004, "用户不存在");
            }

            // 检查可提现金额
            BigDecimal withdrawableBalance = user.getWithdrawableBalance() != null ? user.getWithdrawableBalance() : BigDecimal.ZERO;
            if (withdrawableBalance.compareTo(request.getAmount()) < 0) {
                return Result.error(2002, "可提现金额不足");
            }

            // 获取配置
            Map<String, Object> config = getWalletConfigMap();
            BigDecimal minAmount = new BigDecimal(config.get("minWithdrawalAmount").toString());
            BigDecimal maxAmount = new BigDecimal(config.get("maxWithdrawalAmount").toString());
            BigDecimal dailyLimit = new BigDecimal(config.get("dailyWithdrawalLimit").toString());

            // 检查提现金额限制
            if (request.getAmount().compareTo(minAmount) < 0) {
                return Result.error(2003, "提现金额不能小于" + minAmount + "元");
            }
            if (request.getAmount().compareTo(maxAmount) > 0) {
                return Result.error(2003, "提现金额不能大于" + maxAmount + "元");
            }

            // 检查每日提现限额
            BigDecimal todayAmount = withdrawalRequestMapper.getTodayWithdrawalAmount(userId);
            if (todayAmount.add(request.getAmount()).compareTo(dailyLimit) > 0) {
                return Result.error(2004, "超出每日提现限额");
            }

            // 计算手续费
            BigDecimal fee = calculateWithdrawalFee(request.getAmount());
            BigDecimal actualAmount = request.getAmount().subtract(fee);

            // 创建提现申请
            WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
            withdrawalRequest.setUserId(userId);
            withdrawalRequest.setWithdrawalNo(generateWithdrawalNo());
            withdrawalRequest.setAmount(request.getAmount());
            withdrawalRequest.setFee(fee);
            withdrawalRequest.setActualAmount(actualAmount);
            withdrawalRequest.setPaymentMethod(request.getPaymentMethod());
            withdrawalRequest.setPaymentAccount(request.getPaymentAccount());
            withdrawalRequest.setStatus("pending");
            withdrawalRequest.setRemark(request.getRemark());
            withdrawalRequestMapper.insert(withdrawalRequest);

            // 扣除可提现金额
            updateUserWithdrawableBalance(userId, request.getAmount().negate());

            // 更新用户总提现
            userMapper.updateUserTotalWithdraw(userId, request.getAmount());

            updateUserBalance(userId, request.getAmount().negate());

            userMapper.updateUserServiceFeeTotal(userId, fee);


            // 记录交易
            WalletTransaction transaction = new WalletTransaction();
            transaction.setUserId(userId);
            transaction.setType("expense");
            transaction.setAmount(request.getAmount());
            transaction.setWithdrawableAmount(request.getAmount().negate());
            transaction.setServiceFee(fee);
            transaction.setDescription("提现申请 - " + withdrawalRequest.getWithdrawalNo());
            transaction.setRelatedType("withdrawal");
            transaction.setRelatedId(withdrawalRequest.getId().toString());
            transaction.setTransactionNo(generateTransactionNo());
            transaction.setStatus("completed");
            walletTransactionMapper.insert(transaction);

            processWithdrawal(withdrawalRequest.getId());
            return Result.success(withdrawalRequestMapper.selectById(withdrawalRequest.getId()));
        } catch (Exception e) {
            log.error("提现申请失败", e);
            return Result.error(1005, "提现申请失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Boolean> processOrderIncome(Long orderId) {
        try {
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error(1004, "订单不存在");
            }

            Long artistId = order.getArtistId();
            BigDecimal orderAmount = order.getPrice();

            // 获取配置
            Map<String, Object> config = getWalletConfigMap();
            BigDecimal serviceFeeRate = new BigDecimal(config.get("serviceFeeRate").toString());
            Integer delayDays = Integer.parseInt(config.get("withdrawalDelayDays").toString());

            // 计算收入分配
            BigDecimal artistIncome = orderAmount.multiply(BigDecimal.ONE.subtract(serviceFeeRate));
            BigDecimal serviceFee = orderAmount.multiply(serviceFeeRate);

            // 1. 立即到账到余额
            updateUserBalance(artistId, artistIncome);

            // 2. 记录交易
            WalletTransaction transaction = new WalletTransaction();
            transaction.setUserId(artistId);
            transaction.setType("income");
            transaction.setAmount(artistIncome);
            transaction.setWithdrawableAmount(artistIncome);
            transaction.setServiceFee(serviceFee);
            transaction.setDescription("订单收入 - " + order.getTitle());
            transaction.setRelatedType("order_income");
            transaction.setRelatedId(orderId.toString());
            transaction.setTransactionNo(generateTransactionNo());
            transaction.setStatus("completed");
            walletTransactionMapper.insert(transaction);

            // 3. 创建延迟到账记录
            WalletPendingWithdrawal pending = new WalletPendingWithdrawal();
            pending.setUserId(artistId);
            pending.setOrderId(orderId);
            pending.setAmount(artistIncome);
            pending.setServiceFee(serviceFee);
            pending.setAvailableAt(LocalDateTime.now().plusDays(delayDays));
            pending.setStatus("pending");
            walletPendingWithdrawalMapper.insert(pending);

            // 4. 更新用户统计
            updateUserStatistics(artistId, artistIncome, serviceFee);

            return Result.success(true);
        } catch (Exception e) {
            log.error("处理订单收入失败", e);
            return Result.error(1005, "处理订单收入失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Boolean> processPendingWithdrawals() {
        try {
            // 查询到期的延迟到账记录
            List<WalletPendingWithdrawal> pendingList = walletPendingWithdrawalMapper.getExpiredRecords(LocalDateTime.now());

            for (WalletPendingWithdrawal pending : pendingList) {
                // 更新状态为可提现
                pending.setStatus("available");
                walletPendingWithdrawalMapper.updateById(pending);

                // 更新用户可提现金额
                updateUserWithdrawableBalance(pending.getUserId(), pending.getAmount());

                // 记录交易
                WalletTransaction transaction = new WalletTransaction();
                transaction.setUserId(pending.getUserId());
                transaction.setType("withdrawable_available");
                transaction.setAmount(pending.getAmount());
                transaction.setWithdrawableAmount(pending.getAmount());
                transaction.setServiceFee(pending.getServiceFee());
                transaction.setDescription("延迟到账 - 订单" + pending.getOrderId());
                transaction.setRelatedType("pending_withdrawal");
                transaction.setRelatedId(pending.getOrderId().toString());
                transaction.setTransactionNo(generateTransactionNo());
                transaction.setStatus("completed");
                walletTransactionMapper.insert(transaction);
            }

            return Result.success(true);
        } catch (Exception e) {
            log.error("处理延迟到账失败", e);
            return Result.error(1005, "处理延迟到账失败：" + e.getMessage());
        }
    }

    @Override
    public IPage<WalletTransaction> getTransactions(Long userId, Integer page, Integer pageSize, String type, String relatedType) {
        Page<WalletTransaction> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<WalletTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletTransaction::getUserId, userId);

        if (type != null && !type.isEmpty()) {
            wrapper.eq(WalletTransaction::getType, type);
        }

        if (relatedType != null && !relatedType.isEmpty()) {
            wrapper.eq(WalletTransaction::getRelatedType, relatedType);
        }

        wrapper.orderByDesc(WalletTransaction::getCreatedAt);

        return walletTransactionMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public IPage<WithdrawalRequest> getWithdrawalHistory(Long userId, Integer page, Integer pageSize, String status) {
        Page<WithdrawalRequest> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<WithdrawalRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WithdrawalRequest::getUserId, userId);

        if (status != null && !status.isEmpty()) {
            wrapper.eq(WithdrawalRequest::getStatus, status);
        }

        wrapper.orderByDesc(WithdrawalRequest::getCreatedAt);

        return withdrawalRequestMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public IPage<WalletTransaction> getPendingWithdrawals(Long userId, Integer page, Integer pageSize, String status) {
        // 这里简化处理，实际应该查询延迟到账记录表
        Page<WalletTransaction> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<WalletTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletTransaction::getUserId, userId);
        wrapper.eq(WalletTransaction::getRelatedType, "pending_withdrawal");

        if (status != null && !status.isEmpty()) {
            // 根据状态筛选逻辑
        }

        wrapper.orderByDesc(WalletTransaction::getCreatedAt);

        return walletTransactionMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Result<Object> getWalletConfig() {
        try {
            Map<String, Object> config = getWalletConfigMap();
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取钱包配置失败", e);
            return Result.error(1005, "获取钱包配置失败");
        }
    }

    @Override
    public Result<Object> getWalletStatistics(Long userId, String period) {
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("period", period != null ? period : "month");

            // 这里应该根据period参数计算相应的统计数据
            // 简化处理，返回基础统计信息
            User user = userMapper.selectById(userId);
            if (user != null) {
                Map<String, Object> balance = new HashMap<>();
                balance.put("current", user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO);
                balance.put("withdrawable", user.getWithdrawableBalance() != null ? user.getWithdrawableBalance() : BigDecimal.ZERO);
                balance.put("pending", walletPendingWithdrawalMapper.getPendingAmount(userId));
                statistics.put("balance", balance);
            }

            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取钱包统计失败", e);
            return Result.error(1005, "获取钱包统计失败");
        }
    }

    @Override
    public String generateTransactionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 10000));
        return "TXN" + timestamp + String.format("%04d", Integer.parseInt(random));
    }


    @Override
    public String generateWithdrawalNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 10000));
        return "WD" + timestamp + String.format("%04d", Integer.parseInt(random));
    }

    @Override
    public BigDecimal calculateWithdrawalFee(BigDecimal amount) {
        Map<String, Object> config = getWalletConfigMap();
        BigDecimal feeRate = new BigDecimal(config.get("withdrawalFeeRate").toString());
        BigDecimal minFee = new BigDecimal(config.get("minWithdrawalFee").toString());
        BigDecimal maxFee = new BigDecimal(config.get("maxWithdrawalFee").toString());

        BigDecimal fee = amount.multiply(feeRate);
        if (fee.compareTo(minFee) < 0) {
            fee = minFee;
        }
        if (fee.compareTo(maxFee) > 0) {
            fee = maxFee;
        }

        return fee;
    }

    @Override
    public boolean updateUserBalance(Long userId, BigDecimal amount) {
        return userMapper.updateUserBalance(userId, amount) > 0;
    }

    @Override
    public boolean updateUserWithdrawableBalance(Long userId, BigDecimal amount) {
        return userMapper.updateUserWithdrawableBalance(userId, amount) > 0;
    }

    @Override
    public boolean updateUserStatistics(Long userId, BigDecimal income, BigDecimal serviceFee) {
        return userMapper.updateUserStatistics(userId, income, serviceFee) > 0;
    }

    private WalletTransactionDTO convertToDTO(WalletTransaction transaction) {
        WalletTransactionDTO dto = new WalletTransactionDTO();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setWithdrawableAmount(transaction.getWithdrawableAmount());
        dto.setServiceFee(transaction.getServiceFee());
        dto.setDescription(transaction.getDescription());
        dto.setRelatedType(transaction.getRelatedType());
        // 将String类型的relatedId转换为Long类型
        if (transaction.getRelatedId() != null && !transaction.getRelatedId().isEmpty()) {
            try {
                dto.setRelatedId(Long.parseLong(transaction.getRelatedId()));
            } catch (NumberFormatException e) {
                dto.setRelatedId(null);
            }
        } else {
            dto.setRelatedId(null);
        }
        dto.setAvailableAt(transaction.getAvailableAt());
        dto.setTransactionNo(transaction.getTransactionNo());
        dto.setStatus(transaction.getStatus());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }

    private Map<String, Object> getWalletConfigMap() {
        Map<String, Object> config = new HashMap<>();
        // 默认配置
        config.put("serviceFeeRate", "0.2");
        config.put("withdrawalDelayDays", "7");
        config.put("minWithdrawalAmount", "1.00");
        config.put("maxWithdrawalAmount", "50000.00");
        config.put("dailyWithdrawalLimit", "100000.00");
        config.put("withdrawalFeeRate", "0.02");
        config.put("minWithdrawalFee", "1.00");
        config.put("maxWithdrawalFee", "50.00");
        return config;
    }

    @Override
    @Transactional
    public Result<Boolean> processWithdrawal(Long withdrawalId) {
        try {
            // 查询提现申请
            WithdrawalRequest withdrawalRequest = withdrawalRequestMapper.selectById(withdrawalId);
            if (withdrawalRequest == null) {
                return Result.error(1004, "提现申请不存在");
            }

            if (!"pending".equals(withdrawalRequest.getStatus())) {
                return Result.error(2005, "提现申请状态不正确");
            }

            // 查询用户信息
            User user = userMapper.selectById(withdrawalRequest.getUserId());
            if (user == null) {
                return Result.error(1004, "用户不存在");
            }

            // 检查用户openid
            if (user.getOpenid() == null || user.getOpenid().isEmpty()) {
                return Result.error(2006, "用户未绑定微信");
            }

            // 调用微信企业付款到零钱
            Integer amountInCents = withdrawalRequest.getActualAmount().multiply(new BigDecimal("100")).intValue();

            Map<String, String> transferResult = weChatEnterprisePayService.pay(
                    withdrawalRequest.getWithdrawalNo(), user.getOpenid(),
                    amountInCents,
                    "提现到微信零钱", "127.0.0.1"
            );


//            Map<String, Object> transferResult = wechatPayService.transferToBalance(
//                withdrawalRequest.getWithdrawalNo(),
//                user.getOpenid(),
//                amountInCents,
//                "提现到微信零钱",
//                "NO_CHECK", // 不校验真实姓名
//                null
//            );
            log.debug("transferResult:{}", transferResult);

            boolean success = "SUCCESS".equals(transferResult.get("result_code"));
            if (success) {
                // 更新提现申请状态
                withdrawalRequest.setStatus("success");
                withdrawalRequest.setProcessedAt(LocalDateTime.now());
                withdrawalRequest.setRemark("微信商家转账成功");
                withdrawalRequestMapper.updateById(withdrawalRequest);

                // 记录交易
                WalletTransaction transaction = new WalletTransaction();
                transaction.setUserId(withdrawalRequest.getUserId());
                transaction.setType("withdrawal_success");
                transaction.setAmount(withdrawalRequest.getActualAmount());
                transaction.setWithdrawableAmount(BigDecimal.ZERO);
                transaction.setServiceFee(withdrawalRequest.getFee());
                transaction.setDescription("提现成功 - " + withdrawalRequest.getWithdrawalNo());
                transaction.setRelatedType("withdrawal_success");
                transaction.setRelatedId(withdrawalRequest.getId().toString());
                transaction.setTransactionNo(generateTransactionNo());
                transaction.setStatus("completed");
                walletTransactionMapper.insert(transaction);

                log.info("提现处理成功，提现单号：{}，用户ID：{}，金额：{}",
                        withdrawalRequest.getWithdrawalNo(), withdrawalRequest.getUserId(), withdrawalRequest.getActualAmount());

                return Result.success(true);
            } else {
                // 更新提现申请状态为失败
                withdrawalRequest.setStatus("failed");
                withdrawalRequest.setProcessedAt(LocalDateTime.now());
                withdrawalRequest.setRemark("微信商家转账失败：" + transferResult.get(return_msg));
                withdrawalRequestMapper.updateById(withdrawalRequest);

                // 退还可提现金额
                updateUserWithdrawableBalance(withdrawalRequest.getUserId(), withdrawalRequest.getAmount());

                // 更新用户总提现
                userMapper.updateUserTotalWithdraw(withdrawalRequest.getUserId(), withdrawalRequest.getAmount().negate());

                updateUserBalance(withdrawalRequest.getUserId(), withdrawalRequest.getAmount());

                userMapper.updateUserServiceFeeTotal(withdrawalRequest.getUserId(), withdrawalRequest.getFee().negate());


                log.error("提现处理失败，提现单号：{}，错误：{}",
                        withdrawalRequest.getWithdrawalNo(), transferResult.get(return_msg));

                return Result.error(2007, "提现处理失败：" + transferResult.get(return_msg));
            }

        } catch (Exception e) {
            log.error("处理提现申请失败", e);
            return Result.error(1005, "处理提现申请失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> queryWithdrawalStatus(String withdrawalNo) {
        try {
            // 查询提现申请
            LambdaQueryWrapper<WithdrawalRequest> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WithdrawalRequest::getWithdrawalNo, withdrawalNo);
            WithdrawalRequest withdrawalRequest = withdrawalRequestMapper.selectOne(wrapper);

            if (withdrawalRequest == null) {
                return Result.error(1004, "提现申请不存在");
            }

            // 如果状态是pending，查询微信商家转账状态
            if ("pending".equals(withdrawalRequest.getStatus())) {
                try {
                    Map<String, Object> queryResult = wechatPayService.queryTransferToBalance(withdrawalNo);
                    boolean success = (Boolean) queryResult.get("success");
                    if (success) {
                        String detailStatus = (String) queryResult.get("detailStatus");
                        if ("SUCCESS".equals(detailStatus)) {
                            // 更新状态为成功
                            withdrawalRequest.setStatus("success");
                            withdrawalRequest.setProcessedAt(LocalDateTime.now());
                            withdrawalRequest.setRemark("微信商家转账成功");
                            withdrawalRequestMapper.updateById(withdrawalRequest);
                            return Result.success("success");
                        } else if ("FAILED".equals(detailStatus)) {
                            // 更新状态为失败
                            withdrawalRequest.setStatus("failed");
                            withdrawalRequest.setProcessedAt(LocalDateTime.now());
                            withdrawalRequest.setRemark("微信商家转账失败：" + queryResult.get("failReason"));
                            withdrawalRequestMapper.updateById(withdrawalRequest);

                            // 退还可提现金额
                            updateUserWithdrawableBalance(withdrawalRequest.getUserId(), withdrawalRequest.getAmount());
                            return Result.success("failed");
                        } else {
                            return Result.success("processing");
                        }
                    }
                } catch (Exception e) {
                    log.error("查询微信商家转账状态失败", e);
                }
            }

            return Result.success(withdrawalRequest.getStatus());

        } catch (Exception e) {
            log.error("查询提现状态失败", e);
            return Result.error(1005, "查询提现状态失败：" + e.getMessage());
        }
    }
}
