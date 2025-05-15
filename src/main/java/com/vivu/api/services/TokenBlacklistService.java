package com.vivu.api.services;

import java.time.Instant;

/**
 * Interface for managing blacklisted JWT access tokens.
 */
public interface TokenBlacklistService {

    /**
     * Adds a token identifier (JTI) to the blacklist with an expiration time
     * matching the original token's expiry.
     *
     * @param jti        The JWT ID (unique identifier) of the token to blacklist.
     * @param expiryTime The original expiry time of the JWT token.
     */
    void blacklistToken(String jti, Instant expiryTime);

    /**
     * Checks if a given token identifier (JTI) is currently in the blacklist.
     *
     * @param jti The JWT ID to check.
     * @return true if the token JTI is blacklisted, false otherwise.
     */
    boolean isTokenBlacklisted(String jti);
}