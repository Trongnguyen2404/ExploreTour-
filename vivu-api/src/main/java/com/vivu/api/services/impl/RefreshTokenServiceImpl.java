package com.vivu.api.services.impl;

import com.vivu.api.entities.RefreshToken;
import com.vivu.api.entities.User;
import com.vivu.api.exception.TokenRefreshException; // Tạo exception này ở bước sau
import com.vivu.api.repositories.RefreshTokenRepository;
import com.vivu.api.repositories.UserRepository;
import com.vivu.api.services.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    @Value("${vivu.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository; // Cần để lấy User object

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    @Override
    public Optional<RefreshToken> findByTokenUserId(Integer userId) {
        return refreshTokenRepository.findByUser_Id(userId);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Xóa token cũ của user này nếu có (đảm bảo chỉ có 1 refresh token active mỗi lần)
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .token(UUID.randomUUID().toString()) // Tạo UUID ngẫu nhiên làm token
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        logger.info("Created new refresh token for user {}", userId);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token); // Xóa token hết hạn khỏi DB
            logger.warn("Refresh token was expired and deleted: {}", token.getToken());
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    @Transactional
    public int deleteByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        int deletedCount = refreshTokenRepository.deleteByUser(user);
        logger.info("Deleted {} refresh token(s) for user {}", deletedCount, userId);
        return deletedCount;
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        Instant now = Instant.now();
        logger.info("Running Refresh Token cleanup task at {}", now);
        int deletedCount = refreshTokenRepository.deleteExpiredTokens(now);
        logger.info("Finished Refresh Token cleanup. Deleted {} expired tokens.", deletedCount);
    }
}