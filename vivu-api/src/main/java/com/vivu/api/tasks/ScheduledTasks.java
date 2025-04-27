package com.vivu.api.tasks;

import com.vivu.api.services.OtpService;
import com.vivu.api.services.RefreshTokenService; // Import mới
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private OtpService otpService;

    @Autowired
    private RefreshTokenService refreshTokenService; // Inject mới

    // Chạy mỗi đầu giờ để dọn OTP
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupOtps() {
        logger.info("Running scheduled OTP cleanup...");
        otpService.cleanupExpiredOtps();
        logger.info("Finished scheduled OTP cleanup.");
    }

    // Chạy mỗi ngày vào 2h sáng để dọn Refresh Token
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupRefreshTokens() {
        logger.info("Running scheduled Refresh Token cleanup...");
        refreshTokenService.deleteExpiredTokens();
        logger.info("Finished scheduled Refresh Token cleanup.");
    }
}