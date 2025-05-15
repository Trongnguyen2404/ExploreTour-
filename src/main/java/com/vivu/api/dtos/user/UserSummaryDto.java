package com.vivu.api.dtos.user; // Đặt trong dtos.user hoặc dtos.common

import com.vivu.api.entities.User;
import lombok.Builder;
import lombok.Data;

// DTO chứa thông tin cơ bản của User để hiển thị ở nơi khác (vd: review)
@Data
@Builder
public class UserSummaryDto {
    private Integer id;
    private String fullName;
    private String profilePictureUrl;

    public static UserSummaryDto fromEntity(User user) {
        if (user == null) return null;
        return UserSummaryDto.builder()
                .id(user.getId())
                .fullName(user.getFullName() != null ? user.getFullName() : user.getUsername()) // Hiển thị username nếu chưa có full name
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }
}