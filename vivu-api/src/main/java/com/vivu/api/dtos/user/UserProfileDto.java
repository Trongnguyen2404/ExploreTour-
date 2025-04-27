package com.vivu.api.dtos.user;

import com.vivu.api.entities.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder // Dùng Builder pattern để tạo đối tượng dễ dàng
public class UserProfileDto {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String profilePictureUrl;
    private String role; // Thêm role nếu cần hiển thị
    private Boolean active; // *** THÊM TRƯỜNG NÀY ***
    private Instant createdAt; // Thêm ngày tạo để hiển thị
    private Instant updatedAt; // Có thể thêm nếu cần
    private Instant deletedAt;

    // Phương thức factory để tạo từ User entity
    public static UserProfileDto fromEntity(User user) {
        if (user == null) return null;
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .phoneNumber(user.getPhoneNumber())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole().name())
                .active(user.isActive()) // *** LẤY GIÁ TRỊ TỪ ENTITY ***
                .createdAt(user.getCreatedAt()) // Lấy ngày tạo
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}