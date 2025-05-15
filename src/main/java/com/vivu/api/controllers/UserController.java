package com.vivu.api.controllers;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse; // Import PagedResponse
import com.vivu.api.dtos.user.UpdateProfileRequest;
import com.vivu.api.dtos.user.UpdateUserRoleRequest; // Import DTO mới
import com.vivu.api.dtos.user.UserProfileDto;
import com.vivu.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest; // Import PageRequest
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.data.domain.Sort; // Import Sort
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import PreAuthorize
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users") // Base path cho user endpoints
public class UserController {

    @Autowired
    private UserService userService;

// --- User endpoints ---

    @GetMapping("/profile")
// @PreAuthorize("isAuthenticated()") // Đã được bảo vệ
    public ResponseEntity<UserProfileDto> getUserProfile(Authentication authentication) {
        UserProfileDto userProfile = userService.getUserProfile(authentication);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
// @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> updateUserProfile(@Valid @RequestBody UpdateProfileRequest updateProfileRequest, Authentication authentication) {
        ApiResponse response = userService.updateUserProfile(updateProfileRequest, authentication);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

// --- Admin endpoints ---

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được truy cập
    public ResponseEntity<PagedResponse<UserProfileDto>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PagedResponse<UserProfileDto> response = userService.getAllUsers(pageable); // Service lấy cả inactive
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateUserRole(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateUserRoleRequest request,
            Authentication authentication) {
        ApiResponse response = userService.updateUserRole(userId, request, authentication);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Integer userId, Authentication authentication) {
        // Thực hiện soft delete thông qua service
        ApiResponse response = userService.deleteUser(userId, authentication);
        return ResponseEntity.ok(response);
    }

    // --- MỚI: Endpoint lấy danh sách User Inactive ---
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<UserProfileDto>> getInactiveUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            // Mặc định sắp xếp theo thời gian bị xóa giảm dần
            @RequestParam(value = "sortBy", defaultValue = "deletedAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        // Có thể thêm validation cho sortBy nếu cần
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PagedResponse<UserProfileDto> response = userService.getInactiveUsers(pageable);
        return ResponseEntity.ok(response);
    }

    // --- MỚI: Endpoint Hard Delete User ---
// Sử dụng /force để phân biệt rõ ràng với soft delete
    @DeleteMapping("/{userId}/force")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> hardDeleteUser(
            @PathVariable Integer userId,
            Authentication authentication) {
        // Gọi service thực hiện hard delete
        ApiResponse response = userService.hardDeleteUser(userId, authentication);
        if (response.isSuccess()) {
            // Trả về 200 OK nếu thành công
            return ResponseEntity.ok(response);
        } else {
            // Trả về 400 Bad Request nếu có lỗi logic hoặc validation (vd: admin tự xóa)
            // Hoặc có thể trả 500 nếu là lỗi hệ thống không mong muốn
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{userId}/reactivate")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được khôi phục
    public ResponseEntity<ApiResponse> reactivateUser(
            @PathVariable Integer userId,
            Authentication authentication) {
        ApiResponse response = userService.reactivateUser(userId, authentication);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // Có thể trả về badRequest nếu lỗi do logic (vd: user không tồn tại, đã active)
            // hoặc một status code khác tùy theo bản chất lỗi
            return ResponseEntity.badRequest().body(response);
        }
    }
}