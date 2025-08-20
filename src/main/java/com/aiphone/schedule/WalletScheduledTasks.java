package com.aiphone.schedule;

import com.aiphone.common.Result;
import com.aiphone.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 钱包定时任务
 */
@Slf4j
@Component
public class WalletScheduledTasks {
    
    @Autowired
    private WalletService walletService;
    
    /**
     * 每天凌晨2点处理延迟到账
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void processPendingWithdrawals() {
        log.info("开始处理延迟到账...");
        try {
            Result<Boolean> result = walletService.processPendingWithdrawals();
            if (result.getCode() == 0) {
                log.info("延迟到账处理完成");
            } else {
                log.error("延迟到账处理失败：{}", result.getMessage());
            }
        } catch (Exception e) {
            log.error("延迟到账处理异常", e);
        }
    }
    
    /**
     * 每小时检查一次延迟到账（可选，用于更及时的处理）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkPendingWithdrawals() {
        log.debug("检查延迟到账...");
        try {
            Result<Boolean> result = walletService.processPendingWithdrawals();
            if (result.getCode() == 0) {
                log.debug("延迟到账检查完成");
            }
        } catch (Exception e) {
            log.error("延迟到账检查异常", e);
        }
    }
}
