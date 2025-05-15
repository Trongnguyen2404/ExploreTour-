package com.vivu.api.security.jwt;

import com.vivu.api.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // Sử dụng SecretKey
import java.time.Instant;
import java.util.Date;
import java.util.UUID; // Import UUID để tạo JTI

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${vivu.app.jwtSecret}")
    private String jwtSecret;

    @Value("${vivu.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Tạo key từ secret (trả về SecretKey)
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Cập nhật: Tạo token từ Authentication (Thêm JTI) ---
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Instant now = Instant.now();
        Instant expiryInstant = now.plusMillis(jwtExpirationMs);
        String jti = UUID.randomUUID().toString(); // <<< TẠO JTI >>>

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Thường là email
                .setId(jti) // <<< SET JTI VÀO TOKEN >>>
                .claim("uid", userPrincipal.getId()) // Thêm userId vào claims
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryInstant))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512) // Sử dụng HS512
                .compact();
    }

    // --- Cập nhật: Tạo token từ username (Thêm JTI) ---
    public String generateTokenFromUsername(String username) {
        Instant now = Instant.now();
        Instant expiryInstant = now.plusMillis(jwtExpirationMs);
        String jti = UUID.randomUUID().toString(); // <<< TẠO JTI >>>

        return Jwts.builder()
                .setSubject(username)
                .setId(jti) // <<< SET JTI VÀO TOKEN >>>
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryInstant))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // --- Mới: Lấy JTI từ token ---
    public String getJtiFromJwtToken(String token) {
        // Sử dụng helper để lấy claims, bao gồm cả khi token đã hết hạn
        Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claims.getId() : null;
    }

    // --- Mới: Lấy thời gian hết hạn (Instant) từ token ---
    public Instant getExpiryDateFromJwtToken(String token) {
        // Sử dụng helper để lấy claims, bao gồm cả khi token đã hết hạn
        Claims claims = getAllClaimsFromToken(token);
        Date expirationDate = (claims != null) ? claims.getExpiration() : null;
        return (expirationDate != null) ? expirationDate.toInstant() : null;
    }

    // Lấy username (subject) từ token
    public String getUserNameFromJwtToken(String token) {
        // Sử dụng helper để lấy claims
        Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    // Xác thực token (kiểm tra chữ ký và thời hạn)
    public boolean validateJwtToken(String authToken) {
        try {
            // Chỉ cần parse, nếu không có lỗi là hợp lệ (về chữ ký và thời hạn)
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // Token hết hạn không được coi là hợp lệ cho việc xác thực request
            logger.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty or invalid: {}", e.getMessage());
        } catch (Exception e) { // Bắt các lỗi khác
            logger.error("JWT validation failed: {}", e.getMessage());
        }
        return false;
    }

    // --- Mới: Helper để lấy Claims kể cả khi token hết hạn ---
    // Mục đích là để đọc JTI/Expiry ngay cả khi token đã hết hạn để blacklist
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Nếu hết hạn, vẫn trả về claims để đọc thông tin cần thiết
            logger.trace("Returning claims from expired token: {}", e.getMessage());
            return e.getClaims();
        } catch (Exception e) {
            // Các lỗi khác (chữ ký sai, sai định dạng,...) thì không lấy được claims
            logger.error("Could not get claims from JWT token: {}", e.getMessage());
            return null; // Trả về null nếu không thể parse claims
        }
    }
}