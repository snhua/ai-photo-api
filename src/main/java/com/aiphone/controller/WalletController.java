package com.aiphone.controller;

import com.aiphone.common.Result;
import com.aiphone.dto.WalletInfo;
import com.aiphone.dto.WithdrawalRequestDTO;

import com.aiphone.entity.WithdrawalRequest;
import com.aiphone.entity.WalletTransaction;
import com.aiphone.service.WalletService;
import com.aiphone.util.UserContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 钱包控制器
 */
@Slf4j
@RestController
@RequestMapping("/wallet")
@Api(tags = "钱包相关接口")
@Validated
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    /**
     * 获取钱包信息
     */
    @GetMapping
    @ApiOperation("获取钱包信息")
    public Result<WalletInfo> getWalletInfo() {
        try {
            Long userId = UserContext.getCurrentUserId();
            WalletInfo walletInfo = walletService.getWalletInfo(userId);
            if (walletInfo == null) {
                return Result.error(1004, "用户不存在");
            }
            return Result.success(walletInfo);
        } catch (Exception e) {
            log.error("获取钱包信息失败", e);
            return Result.error(1005, "获取钱包信息失败：" + e.getMessage());
        }
    }


    
    /**
     * 提现申请
     */
    @PostMapping("/withdraw")
    @ApiOperation("提现申请")
    public Result<WithdrawalRequest> withdraw(@Valid @RequestBody WithdrawalRequestDTO request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return walletService.withdraw(userId, request);
        } catch (Exception e) {
            log.error("提现申请失败", e);
            return Result.error(1005, "提现申请失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取交易记录
     */
    @GetMapping("/transactions")
    @ApiOperation("获取交易记录")
    public Result<IPage<WalletTransaction>> getTransactions(
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize,
            @ApiParam("交易类型") @RequestParam(required = false) String type,
            @ApiParam("关联类型") @RequestParam(required = false) String relatedType) {
        try {
            Long userId = UserContext.getCurrentUserId();
            IPage<WalletTransaction> transactions = walletService.getTransactions(userId, page, pageSize, type, relatedType);
            return Result.success(transactions);
        } catch (Exception e) {
            log.error("获取交易记录失败", e);
            return Result.error(1005, "获取交易记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取提现记录
     */
    @GetMapping("/withdrawals")
    @ApiOperation("获取提现记录")
    public Result<IPage<WithdrawalRequest>> getWithdrawalHistory(
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize,
            @ApiParam("提现状态") @RequestParam(required = false) String status) {
        try {
            Long userId = UserContext.getCurrentUserId();
            IPage<WithdrawalRequest> withdrawals = walletService.getWithdrawalHistory(userId, page, pageSize, status);

            return Result.success(withdrawals);
        } catch (Exception e) {
            log.error("获取提现记录失败", e);
            return Result.error(1005, "获取提现记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取延迟到账记录
     */
    @GetMapping("/pending-withdrawals")
    @ApiOperation("获取延迟到账记录")
    public Result<IPage<WalletTransaction>> getPendingWithdrawals(
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        try {
            Long userId = UserContext.getCurrentUserId();
            IPage<WalletTransaction> pendingWithdrawals = walletService.getPendingWithdrawals(userId, page, pageSize, status);
            return Result.success(pendingWithdrawals);
        } catch (Exception e) {
            log.error("获取延迟到账记录失败", e);
            return Result.error(1005, "获取延迟到账记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取钱包配置
     */
    @GetMapping("/config")
    @ApiOperation("获取钱包配置")
    public Result<Object> getWalletConfig() {
        try {
            return walletService.getWalletConfig();
        } catch (Exception e) {
            log.error("获取钱包配置失败", e);
            return Result.error(1005, "获取钱包配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取钱包统计
     */
    @GetMapping("/statistics")
    @ApiOperation("获取钱包统计")
    public Result<Object> getWalletStatistics(
            @ApiParam("统计周期") @RequestParam(defaultValue = "month") String period) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return walletService.getWalletStatistics(userId, period);
        } catch (Exception e) {
            log.error("获取钱包统计失败", e);
            return Result.error(1005, "获取钱包统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 处理订单收入（内部接口，由订单服务调用）
     */
    @PostMapping("/process-order-income/{orderId}")
    @ApiOperation("处理订单收入")
    public Result<Boolean> processOrderIncome(@ApiParam("订单ID") @PathVariable Long orderId) {
        try {
            return walletService.processOrderIncome(orderId);
        } catch (Exception e) {
            log.error("处理订单收入失败", e);
            return Result.error(1005, "处理订单收入失败：" + e.getMessage());
        }
    }
    
    /**
     * 处理延迟到账（定时任务调用）
     */
    @PostMapping("/process-pending-withdrawals")
    @ApiOperation("处理延迟到账")
    public Result<Boolean> processPendingWithdrawals() {
        try {
            return walletService.processPendingWithdrawals();
        } catch (Exception e) {
            log.error("处理延迟到账失败", e);
            return Result.error(1005, "处理延迟到账失败：" + e.getMessage());
        }
    }
    
    /**
     * 处理提现申请（调用微信企业付款）
     */
//    @PostMapping("/process-withdrawal/{withdrawalId}")
    @ApiOperation("处理提现申请")
    public Result<Boolean> processWithdrawal(@ApiParam("提现申请ID") @PathVariable Long withdrawalId) {
        try {
            return walletService.processWithdrawal(withdrawalId);
        } catch (Exception e) {
            log.error("处理提现申请失败", e);
            return Result.error(1005, "处理提现申请失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询提现状态
     */
    @GetMapping("/withdrawal/{withdrawalNo}/status")
    @ApiOperation("查询提现状态")
    public Result<String> queryWithdrawalStatus(@ApiParam("提现单号") @PathVariable String withdrawalNo) {
        try {
            return walletService.queryWithdrawalStatus(withdrawalNo);
        } catch (Exception e) {
            log.error("查询提现状态失败", e);
            return Result.error(1005, "查询提现状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
