package com.vivu.api.config;

import com.vivu.api.security.jwt.AuthEntryPointJwt;
import com.vivu.api.security.jwt.JwtAuthTokenFilter;
import com.vivu.api.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // *** THÊM IMPORT NÀY ***
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity // Bật tính năng Security của Spring Web
@EnableMethodSecurity(prePostEnabled = true) // Bật annotation @PreAuthorize, @PostAuthorize nếu cần
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private JwtAuthTokenFilter jwtAuthTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Cấu hình domain cụ thể cho production
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // --- Endpoints Xác thực/Quản lý tài khoản ---
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/register/**").permitAll() // Cả 3 bước đăng ký
                        .requestMatchers("/api/v1/auth/forgot-password").permitAll() // Bước 1 quên MK
                        .requestMatchers("/api/v1/auth/forgot-password/verify-otp").permitAll() // *** ĐÃ THÊM *** Bước 2 quên MK
                        .requestMatchers("/api/v1/auth/forgot-password/set-new-password").permitAll() // *** ĐÃ THÊM *** Bước 3 quên MK
                        .requestMatchers("/api/v1/auth/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/me/chatbot-history").authenticated()
                        // --- Endpoints Công khai (Chỉ cho phép đọc - GET) ---
                        .requestMatchers(HttpMethod.GET, "/api/v1/tours/**").permitAll() // GET tours & chi tiết tour
                        .requestMatchers(HttpMethod.GET, "/api/v1/locations/**").permitAll() // GET locations & chi tiết location
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews").permitAll() // GET reviews theo target

                        // --- Endpoints Yêu cầu Xác thực (Đã đăng nhập - Bất kỳ vai trò nào) ---
                        // Mọi request tới các path này mà không phải GET sẽ yêu cầu authenticated() do dòng cuối
                        .requestMatchers("/api/v1/users/profile").authenticated() // Xem, sửa profile
                        .requestMatchers("/api/v1/auth/change-password").authenticated() // Đổi MK đã đăng nhập
                        .requestMatchers("/api/v1/auth/verify-password-for-email-change").authenticated() // Bước 1 đổi email
                        .requestMatchers("/api/v1/auth/request-email-change").authenticated() // Bước 2 đổi email
                        .requestMatchers("/api/v1/auth/verify-email-change").authenticated() // Bước 3 đổi email
                        .requestMatchers("/api/v1/auth/logout").authenticated() // Logout
                        .requestMatchers("/api/v1/reviews/**").authenticated() // POST, PUT, DELETE review
                        .requestMatchers("/api/v1/favorites/**").authenticated() // GET, POST, DELETE favorite

                        // --- Endpoints Yêu cầu Vai trò Admin ---
                        // Sử dụng @PreAuthorize("hasRole('ADMIN')") trong Controller cho các endpoint này là đủ
                        // Tuy nhiên, có thể thêm ở đây để bảo vệ thêm một lớp nữa (tùy chọn)
                        // .requestMatchers(HttpMethod.POST, "/api/v1/tours").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.PUT, "/api/v1/tours/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.DELETE, "/api/v1/tours/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.POST, "/api/v1/locations").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.PUT, "/api/v1/locations/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.DELETE, "/api/v1/locations/**").hasRole("ADMIN")
                        // .requestMatchers("/api/v1/users/**").hasRole("ADMIN") // Quản lý user

                        // --- Mặc định: Tất cả các request khác đều yêu cầu xác thực ---
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}