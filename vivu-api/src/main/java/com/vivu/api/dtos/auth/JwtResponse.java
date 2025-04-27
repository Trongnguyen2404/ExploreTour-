package com.vivu.api.dtos.auth;

import com.vivu.api.security.services.UserDetailsImpl;
import lombok.Data;
import lombok.NoArgsConstructor; // Thêm NoArgsConstructor nếu cần

import java.util.List;

@Data
@NoArgsConstructor
// @AllArgsConstructor // Xóa hoặc sửa AllArgsConstructor nếu có
public class JwtResponse {
    private String token; // Access Token
    private String type = "Bearer";
    private String refreshToken; // Thêm Refresh Token
    private Integer id;
    private String username;
    private String email;
    private List<String> roles;

    // Constructor cập nhật
    public JwtResponse(String accessToken, String refreshToken, UserDetailsImpl userDetails) {
        this.token = accessToken;
        this.refreshToken = refreshToken; // Gán refresh token
        this.id = userDetails.getId();
        this.username = userDetails.getUsername();
        this.email = userDetails.getEmail();
        this.roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();
    }

    // Constructor cũ (nếu cần cho trường hợp khác không có refresh token)
    public JwtResponse(String accessToken, UserDetailsImpl userDetails) {
        this(accessToken, null, userDetails); // Gọi constructor chính với refresh token là null
    }
}