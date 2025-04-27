package com.vivu.api.services.impl;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.user.UpdateProfileRequest;
import com.vivu.api.dtos.user.UpdateUserRoleRequest;
import com.vivu.api.dtos.user.UserProfileDto;
import com.vivu.api.entities.User;
import com.vivu.api.repositories.UserRepository;
import com.vivu.api.security.services.UserDetailsImpl;
import com.vivu.api.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userDetails.getId()));
        return UserProfileDto.fromEntity(currentUser);
    }

    @Override
    @Transactional
    public ApiResponse updateUserProfile(UpdateProfileRequest updateProfileRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userDetails.getId()));

        if (updateProfileRequest.getFullName() != null) {
            currentUser.setFullName(updateProfileRequest.getFullName().trim());
        }
        if (updateProfileRequest.getDateOfBirth() != null) {
            currentUser.setDateOfBirth(updateProfileRequest.getDateOfBirth());
        }
        if (updateProfileRequest.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(updateProfileRequest.getPhoneNumber().trim());
        }
        if (updateProfileRequest.getProfilePictureUrl() != null) {
            currentUser.setProfilePictureUrl(updateProfileRequest.getProfilePictureUrl().trim());
        }

        userRepository.save(currentUser);
        logger.info("User profile updated successfully for user: {}", currentUser.getEmail());
        return new ApiResponse(true, "Profile updated successfully.");
    }

    @Override
    public ApiResponse logoutUser(Authentication authentication) {
        if (authentication != null) {
            logger.info("User logged out: {}", authentication.getName());
        }
        return new ApiResponse(true, "Logged out successfully.");
    }

    // --- Admin Functionalities ---

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserProfileDto> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAllIgnoringStatus(pageable); // Sử dụng phương thức bỏ qua @Where

        List<UserProfileDto> userDtos = userPage.getContent().stream()
                .map(UserProfileDto::fromEntity)
                .collect(Collectors.toList());

        return mapPageToPagedResponse(userPage, UserProfileDto::fromEntity);
    }

    @Override
    @Transactional
    public ApiResponse updateUserRole(Integer userId, UpdateUserRoleRequest request, Authentication adminAuth) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) adminAuth.getPrincipal();
        User adminUser = userRepository.findById(adminDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        User userToUpdate = userRepository.findById(userId) // Tìm user active
                .orElseThrow(() -> new EntityNotFoundException("Active user not found with id: " + userId));

        if (userToUpdate.getId().equals(adminUser.getId())) {
            return new ApiResponse(false, "Admin cannot change their own role.");
        }

        if (userToUpdate.getRole() == request.getRole()) {
            return new ApiResponse(true, "User role is already set to " + request.getRole());
        }

        userToUpdate.setRole(request.getRole());
        userRepository.save(userToUpdate);
        logger.info("User {} role updated to {} by admin {}", userId, request.getRole(), adminUser.getEmail());
        return new ApiResponse(true, "User role updated successfully.");
    }

    @Override
    @Transactional
    public ApiResponse deleteUser(Integer userId, Authentication adminAuth) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) adminAuth.getPrincipal();
        User adminUser = userRepository.findById(adminDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        User userToDelete = userRepository.findById(userId) // Tìm user active để xóa
                .orElseThrow(() -> new EntityNotFoundException("Active user not found with id: " + userId));

        if (userToDelete.getId().equals(adminUser.getId())) {
            return new ApiResponse(false, "Admin cannot delete themselves.");
        }

        // Thực hiện soft delete thủ công
        userToDelete.setActive(false);
        userToDelete.setDeletedAt(Instant.now());
        userToDelete.setDeletedByAdmin(adminUser);
        userRepository.save(userToDelete);

        logger.info("User {} soft deleted by admin {}", userId, adminUser.getEmail());
        return new ApiResponse(true, "User deleted successfully.");
    }

    private <T, U> PagedResponse<U> mapPageToPagedResponse(Page<T> page, java.util.function.Function<T, U> mapper) {
        List<U> dtoList = page.getContent().stream().map(mapper).collect(Collectors.toList());
        return new PagedResponse<>(
                dtoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}