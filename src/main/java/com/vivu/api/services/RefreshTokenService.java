package com.vivu.api.services;

import com.vivu.api.entities.RefreshToken;
import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByTokenUserId(Integer userId);
    RefreshToken createRefreshToken(Integer userId);
    RefreshToken verifyExpiration(RefreshToken token);
    int deleteByUserId(Integer userId);
    void deleteExpiredTokens(); // For scheduled task
}