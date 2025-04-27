package com.vivu.api.services;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse; // Import mới
import com.vivu.api.dtos.user.UpdateProfileRequest;
import com.vivu.api.dtos.user.UpdateUserRoleRequest; // Import mới
import com.vivu.api.dtos.user.UserProfileDto;
import org.springframework.data.domain.Pageable; // Import mới
import org.springframework.security.core.Authentication;

public interface UserService {
    // User endpoints
    UserProfileDto getUserProfile(Authentication authentication);
    ApiResponse updateUserProfile(UpdateProfileRequest updateProfileRequest, Authentication authentication);
    ApiResponse logoutUser(Authentication authentication);

    // Admin endpoints
    PagedResponse<UserProfileDto> getAllUsers(Pageable pageable); // Lấy cả user inactive
    ApiResponse updateUserRole(Integer userId, UpdateUserRoleRequest request, Authentication adminAuth);
    ApiResponse deleteUser(Integer userId, Authentication adminAuth); // Soft delete
}