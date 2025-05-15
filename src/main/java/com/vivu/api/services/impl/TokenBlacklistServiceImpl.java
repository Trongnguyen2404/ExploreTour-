package com.vivu.api.services.impl;

import com.vivu.api.services.TokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistServiceImpl.class);
    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:"; // Tiền tố cho key trong Redis

    @Autowired
    private StringRedisTemplate redisTemplate; // Dùng StringRedisTemplate cho key/value String

    @Override
    public void blacklistToken(String jti, Instant expiryTime) {
        if (jti == null || expiryTime == null) {
            logger.warn("Attempted to blacklist token with null jti or expiryTime.");
            return;
        }

        Instant now = Instant.now();
        // Chỉ blacklist nếu token chưa thực sự hết hạn
        if (expiryTime.isAfter(now)) {
            // Tính thời gian còn lại (TTL) cho key trong Redis
            long ttlSeconds = Duration.between(now, expiryTime).getSeconds();

            // Chỉ lưu vào Redis nếu thời gian còn lại > 0
            if (ttlSeconds > 0) {
                String key = BLACKLIST_KEY_PREFIX + jti;
                try {
                    // Lưu jti vào blacklist. Giá trị value không quan trọng (vd: "1").
                    // Đặt TTL bằng thời gian còn lại của token.
                    redisTemplate.opsForValue().set(key, "1", ttlSeconds, TimeUnit.SECONDS);
                    logger.debug("Token JTI {} blacklisted with TTL {} seconds", jti, ttlSeconds);
                } catch (Exception e) {
                    // Ghi log lỗi nếu không thể kết nối hoặc lưu vào Redis
                    logger.error("Failed to blacklist token JTI {} in Redis: {}", jti, e.getMessage(), e);
                }
            } else {
                logger.debug("Token JTI {} is already effectively expired (TTL <= 0), no need to blacklist.", jti);
            }
        } else {
            logger.debug("Token JTI {} is already expired, no need to blacklist.", jti);
        }
    }

    @Override
    public boolean isTokenBlacklisted(String jti) {
        if (jti == null) {
            return false;
        }
        String key = BLACKLIST_KEY_PREFIX + jti;
        try {
            // Kiểm tra sự tồn tại của key trong Redis
            Boolean hasKey = redisTemplate.hasKey(key);
            return hasKey != null && hasKey;
        } catch (Exception e) {
            // Nếu lỗi kết nối Redis, tạm thời coi như không bị blacklist để tránh khóa oan user
            logger.error("Failed to check blacklist status for token JTI {} in Redis: {}. Assuming not blacklisted.", jti, e.getMessage());
            return false;
        }
    }
}