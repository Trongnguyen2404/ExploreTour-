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

    // Admin endpoints
    PagedResponse<UserProfileDto> getAllUsers(Pageable pageable); // Lấy cả user inactive

    ApiResponse updateUserRole(Integer userId, UpdateUserRoleRequest request, Authentication adminAuth);

    ApiResponse deleteUser(Integer userId, Authentication adminAuth); // Soft delete

    ApiResponse logoutUser(Authentication authentication, String accessToken);


// --- MỚI: Khai báo các phương thức mới ---

    /**
     * Retrieves a paginated list of inactive (soft-deleted) users. (Admin only)
     *
     * @param pageable Pagination information.
     * @return A PagedResponse containing inactive user profiles.
     */
    PagedResponse<UserProfileDto> getInactiveUsers(Pageable pageable);

    /**
     * Permanently deletes a user and all associated data from the system. (Admin only)
     * This is a HARD DELETE operation and is irreversible. Handles related data removal.
     *
     * @param userId    The ID of the user to permanently delete.
     * @param adminAuth Authentication object of the performing admin.
     * @return ApiResponse indicating the result of the operation.
     */
    ApiResponse hardDeleteUser(Integer userId, Authentication adminAuth);
    ApiResponse reactivateUser(Integer userId, Authentication adminAuth);
}