package com.vivu.api.security.jwt;

import com.vivu.api.services.TokenBlacklistService; // <<< THÊM IMPORT
import com.vivu.api.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    @Autowired private JwtUtils jwtUtils;
    @Autowired private UserDetailsServiceImpl userDetailsService;
    @Autowired private TokenBlacklistService tokenBlacklistService; // <<< INJECT

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                String jti = null;
                boolean isValidForProcessing = false; // Cờ để biết token có thể dùng để xác thực không

                try {
                    // 1. Lấy JTI từ token (ngay cả khi đã hết hạn)
                    jti = jwtUtils.getJtiFromJwtToken(jwt);

                    // 2. Kiểm tra xem JTI có trong blacklist không
                    if (jti != null && tokenBlacklistService.isTokenBlacklisted(jti)) {
                        logger.warn("Authentication attempt with blacklisted token JTI: {}", jti);
                        // Không throw exception, chỉ cần không xác thực và để request đi tiếp
                        // (hoặc có thể gửi lỗi 401 ngay tại đây nếu muốn)
                        // SecurityContextHolder.clearContext(); // Đảm bảo context sạch
                        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been invalidated");
                        // filterChain.doFilter(request, response); // Chuyển tiếp nhưng không có Authentication
                        // return; // Dừng xử lý ở đây nếu muốn gửi lỗi ngay
                    } else {
                        // 3. Nếu không bị blacklist, mới tiến hành validate chữ ký và thời hạn
                        if (jwtUtils.validateJwtToken(jwt)) {
                            isValidForProcessing = true; // Token hợp lệ để xác thực
                        }
                    }
                } catch (Exception e) {
                    // Lỗi khi parse JTI hoặc validate (vd: chữ ký sai, hết hạn...)
                    logger.error("Could not process JWT token: {}", e.getMessage());
                    // Không cần làm gì thêm, isValidForProcessing vẫn là false
                }


                // 4. Nếu token hợp lệ và không bị blacklist, tiến hành xác thực
                if (isValidForProcessing) {
                    try {
                        String username = jwtUtils.getUserNameFromJwtToken(jwt);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails,
                                        null, // Credentials là null vì đã xác thực bằng JWT
                                        userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Thiết lập Authentication vào SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("Successfully authenticated user: {}", username);
                    } catch (Exception e) {
                        logger.error("Cannot set user authentication after validating token: {}", e.getMessage());
                        SecurityContextHolder.clearContext(); // Xóa context nếu có lỗi khi load user/set auth
                    }
                } else {
                    // Nếu token không hợp lệ (hết hạn, sai chữ ký, bị blacklist) thì đảm bảo context sạch
                    if (SecurityContextHolder.getContext().getAuthentication() != null) {
                        logger.debug("Clearing SecurityContext due to invalid or blacklisted token.");
                        SecurityContextHolder.clearContext();
                    }
                }
            }
        } catch (Exception e) {
            // Lỗi không mong muốn trong filter
            logger.error("Cannot set user authentication: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext(); // Đảm bảo an toàn
        }

        // Luôn gọi filter tiếp theo trong chuỗi
        filterChain.doFilter(request, response);
    }

    // Helper để lấy token từ header "Authorization: Bearer <token>"
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION); // Sử dụng HttpHeaders

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Bỏ "Bearer "
        }

        return null;
    }
}