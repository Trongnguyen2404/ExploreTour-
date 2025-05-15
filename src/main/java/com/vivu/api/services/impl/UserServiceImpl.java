package com.vivu.api.services.impl;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.user.UpdateProfileRequest;
import com.vivu.api.dtos.user.UpdateUserRoleRequest;
import com.vivu.api.dtos.user.UserProfileDto;
import com.vivu.api.entities.User;
import com.vivu.api.enums.Role;
import com.vivu.api.repositories.*;
import com.vivu.api.security.jwt.JwtUtils;
import com.vivu.api.security.services.UserDetailsImpl;
import com.vivu.api.services.RefreshTokenService;
import com.vivu.api.services.TokenBlacklistService;
import com.vivu.api.services.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @Autowired
    private RefreshTokenService refreshTokenService; // <<< INJECT
    @Autowired
    private TokenBlacklistService tokenBlacklistService; // <<< INJECT
    @Autowired
    private JwtUtils jwtUtils; // <<< INJECT
    @Autowired
    private ChatHistoryRepository chatHistoryRepository; // <<< MỚI
    @Autowired
    private OtpRepository otpRepository;             // <<< MỚI
    @Autowired
    private LocationRepository locationRepository;     // <<< MỚI
    @Autowired
    private TourRepository tourRepository;           // <<< MỚI
    @Autowired
    private ReviewRepository reviewRepository;     // <<< INJECT MỚI
    @Autowired
    private FavoriteRepository favoriteRepository;   // <<< INJECT MỚI
    @PersistenceContext // <<== THÊM DÒNG NÀY
    private EntityManager entityManager;

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
    @Transactional // Quan trọng: Đảm bảo cả blacklist và xóa refresh token thành công hoặc cả hai cùng rollback
    public ApiResponse logoutUser(Authentication authentication, String accessToken) {
        // Kiểm tra xem người dùng đã thực sự đăng nhập chưa
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            logger.warn("Logout attempt without valid authentication.");
            // Có thể trả về lỗi hoặc chỉ đơn giản là thông báo đã logout (vì không có session nào)
            return new ApiResponse(true, "Logout processed (no active session found).");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Integer userId = userDetails.getId();
        String userIdentifier = userDetails.getUsername(); // Email hoặc username

        logger.info("Processing logout for user: {}", userIdentifier);

        try {
            // 1. Blacklist Access Token hiện tại (nếu được cung cấp)
            if (accessToken != null) {
                String jti = jwtUtils.getJtiFromJwtToken(accessToken);
                Instant expiry = jwtUtils.getExpiryDateFromJwtToken(accessToken);

                if (jti != null && expiry != null) {
                    tokenBlacklistService.blacklistToken(jti, expiry);
                    logger.info("Access Token JTI {} added to blacklist for user {}", jti, userIdentifier);
                } else {
                    logger.warn("Could not extract JTI or Expiry from the provided access token for user {}. Token not blacklisted.", userIdentifier);
                }
            } else {
                logger.warn("Access token not provided in logout request for user {}. Cannot blacklist.", userIdentifier);
            }

            // 2. Xóa Refresh Token của User khỏi Database
            // Giả định bạn đã có RefreshTokenService và phương thức deleteByUserId
            int deletedCount = refreshTokenService.deleteByUserId(userId);
            if (deletedCount > 0) {
                logger.info("Deleted {} refresh token(s) for user {}", deletedCount, userIdentifier);
            } else {
                logger.info("No refresh token found to delete for user {}", userIdentifier);
            }

            // 3. Trả về thành công
            logger.info("User {} logged out successfully.", userIdentifier);
            return new ApiResponse(true, "Logged out successfully.");

        } catch (Exception e) {
            // Ghi log lỗi chi tiết
            logger.error("Error during logout process for user {}: {}", userIdentifier, e.getMessage(), e);
            // Transactional sẽ tự động rollback nếu có RuntimeException không được bắt
            // Trả về lỗi cho client
            return new ApiResponse(false, "An error occurred during logout. Please try again.");
        }
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

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserProfileDto> getInactiveUsers(Pageable originalPageable) {
        // Log pageable gốc nhận từ controller (chứa sort)
        logger.info("Fetching inactive users with original pagination request: {}", originalPageable);

        // *** TẠO PAGEABLE MỚI KHÔNG CÓ SORT INFO ***
        // Lấy thông tin page và size từ pageable gốc
        // Bỏ qua thông tin sort (originalPageable.getSort())
        Pageable pageableWithoutSort = PageRequest.of(
                originalPageable.getPageNumber(),
                originalPageable.getPageSize()
                // KHÔNG truyền Sort vào đây
        );
        logger.debug("Calling repository with sort-less pageable: {}", pageableWithoutSort);

        // Gọi phương thức native query (có ORDER BY cố định) với pageable KHÔNG CÓ SORT
        Page<User> inactiveUserPage = userRepository.findInactiveUsersNativelyOrderByDeletedAtDesc(pageableWithoutSort); // <<< GỌI VỚI PAGEABLE MỚI

        logger.info("Found {} inactive users on page {}", inactiveUserPage.getNumberOfElements(), originalPageable.getPageNumber()); // Log page gốc để dễ theo dõi
        // Vẫn dùng helper cũ để map
        return mapPageToPagedResponse(inactiveUserPage, UserProfileDto::fromEntity);
    }

    // --- MỚI: Triển khai Hard Delete User ---
    @Override
    @Transactional // !! CỰC KỲ QUAN TRỌNG !!
    public ApiResponse hardDeleteUser(Integer userId, Authentication adminAuth) {
        // Lấy thông tin admin thực hiện hành động
        UserDetailsImpl adminDetails = (UserDetailsImpl) adminAuth.getPrincipal();
        String adminUsername = adminDetails.getUsername();
        logger.warn("ADMIN ACTION: Attempting HARD DELETE for user ID: {} by Admin: {}", userId, adminUsername);

        // 1. Xác thực Admin và kiểm tra không tự xóa mình
        User adminUser = userRepository.findById(adminDetails.getId())
                .orElseThrow(() -> {
                    logger.error("Admin user {} performing hard delete action not found in DB.", adminUsername);
                    return new EntityNotFoundException("Performing admin user not found.");
                });
        if (userId.equals(adminDetails.getId())) {
            logger.error("Admin {} attempted to hard delete themselves.", adminUsername);
            return new ApiResponse(false, "Admin cannot hard delete themselves.");
        }

        // 2. Tìm User cần xóa bằng Native Query (để bỏ qua @Where)
        User userToDelete = userRepository.findUserByIdNatively(userId)
                .orElseThrow(() -> {
                    logger.warn("User with ID {} not found using native query for hard delete.", userId);
                    return new EntityNotFoundException("User to delete not found with id: " + userId);
                });

        // Lấy thông tin user sắp bị xóa để log và xóa OTP
        String userToDeleteUsername = userToDelete.getUsername();
        String userToDeleteEmail = userToDelete.getEmail();
        logger.info("Found user to hard delete using native query: {} ({})", userToDeleteUsername, userToDeleteEmail);

        try {
            // 3. Xóa/Cập nhật tất cả dữ liệu phụ thuộc TRƯỚC KHI xóa User

            // 3.1 Xóa Refresh Tokens
            logger.debug("[Hard Delete {}] Deleting associated refresh tokens...", userToDeleteUsername);
            refreshTokenService.deleteByUserId(userId);
            logger.info("[Hard Delete {}] Deleted associated refresh token(s).", userToDeleteUsername); // Log số lượng nếu phương thức trả về

            // 3.2 Xóa Chat History
            logger.debug("[Hard Delete {}] Deleting associated chat history...", userToDeleteUsername);
            long deletedChats = chatHistoryRepository.deleteByUserId(userId); // Gọi phương thức xóa
            logger.info("[Hard Delete {}] Deleted {} chat history record(s).", userToDeleteUsername, deletedChats);

            // 3.3 Xóa OTPs theo email
            logger.debug("[Hard Delete {}] Deleting associated OTPs for email {}...", userToDeleteUsername, userToDeleteEmail);
            otpRepository.deleteByEmail(userToDeleteEmail); // Gọi phương thức xóa
            logger.info("[Hard Delete {}] Deleted OTPs for email {}.", userToDeleteUsername, userToDeleteEmail);

            // 3.4 Xóa Reviews của User này
            logger.debug("[Hard Delete {}] Deleting associated reviews...", userToDeleteUsername);
            reviewRepository.deleteByUser(userToDelete); // Gọi phương thức xóa mới
            logger.info("[Hard Delete {}] Deleted associated reviews.", userToDeleteUsername);

            // 3.5 Xóa Favorites của User này
            logger.debug("[Hard Delete {}] Deleting associated favorites...", userToDeleteUsername);
            favoriteRepository.deleteByUser(userToDelete); // Gọi phương thức xóa mới
            logger.info("[Hard Delete {}] Deleted associated favorites.", userToDeleteUsername);

            // 3.6 Nullify Admin references trong Location và Tour (nếu user là Admin)
            if (userToDelete.getRole() == Role.ADMIN) {
                logger.warn("[Hard Delete {}] User is an ADMIN. Nullifying references in Location/Tour...", userToDeleteUsername);
                locationRepository.nullifyCreatedByAdmin(userToDelete);
                locationRepository.nullifyUpdatedByAdmin(userToDelete);
                tourRepository.nullifyCreatedByAdmin(userToDelete);
                tourRepository.nullifyUpdatedByAdmin(userToDelete);
                logger.info("[Hard Delete {}] Nullified admin references in Location/Tour.", userToDeleteUsername);
            }

            // 3.7 (Tùy chọn) Xử lý các bảng khác nếu có khóa ngoại đến User mà không dùng Cascade
            // logger.debug("[Hard Delete {}] Handling other dependencies if any...", userToDeleteUsername);
            // otherRepository.deleteByUser(userToDelete); // Ví dụ

            // 4. Thực hiện Hard Delete User bằng Native Query (để bỏ qua @SQLDelete)
            logger.warn("[Hard Delete {}] Proceeding with native hard delete of User entity ID: {}", userToDeleteUsername, userId);
            userRepository.hardDeleteUserById(userId); // Gọi phương thức native delete
            logger.warn("ADMIN ACTION: HARD DELETE successful for user ID: {}, Username: {} by Admin: {}", userId, userToDeleteUsername, adminUsername);

            return new ApiResponse(true, "User permanently deleted successfully.");

        } catch (DataIntegrityViolationException e) {
            // Lỗi này giờ ít khả năng xảy ra hơn nếu đã xử lý hết khóa ngoại
            logger.error("HARD DELETE FAILED for user ID: {} due to Data Integrity Violation. Possible missed dependency. Admin: {}. Error: {}", userId, adminUsername, e.getMessage(), e);
            return new ApiResponse(false, "Failed to permanently delete user due to data integrity issues. Check server logs.");
        }
        catch (Exception e) {
            // Bắt các lỗi không mong muốn khác
            logger.error("HARD DELETE FAILED for user ID: {} by Admin: {}. Unexpected error: {}", userId, adminUsername, e.getMessage(), e);
            return new ApiResponse(false, "An unexpected error occurred during permanent deletion. Check server logs. Error: " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public ApiResponse reactivateUser(Integer userId, Authentication adminAuth) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) adminAuth.getPrincipal();
        // Admin thực hiện hành động này vẫn nên được lấy bằng cách thông thường (phải active)
        User adminUser = userRepository.findById(adminDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("Admin user performing action not found with id: " + adminDetails.getId()));

        logger.info("Attempting to find user with ID {} for reactivation using EntityManager (Native Query).", userId);

        User userToReactivate;
        try {
            // Sử dụng Native Query với EntityManager để bỏ qua @Where
            // Query này sẽ trả về User entity nếu tìm thấy, bất kể is_active
            // Lưu ý: entityManager.find(User.class, userId) SẼ VẪN BỊ ẢNH HƯỞNG BỞI @Where
            // nên chúng ta phải dùng createNativeQuery hoặc createQuery (JPQL)
            userToReactivate = (User) entityManager.createNativeQuery("SELECT * FROM users WHERE user_id = :userId", User.class)
                    .setParameter("userId", userId)
                    .getSingleResult(); // Ném NoResultException nếu không tìm thấy
        } catch (NoResultException e) {
            logger.warn("EntityManager (Native Query): User (active or inactive) not found with id: {} to reactivate.", userId);
            throw new EntityNotFoundException("User (active or inactive) not found with id: " + userId + " to reactivate. (Searched with EntityManager)");
        }

        // Kiểm tra xem user có thực sự đang bị deactive không
        if (userToReactivate.isActive()) {
            logger.info("User {} is already active. No reactivation needed.", userId);
            return new ApiResponse(true, "User is already active.");
        }

        // Thực hiện khôi phục
        userToReactivate.setActive(true);
        userToReactivate.setDeletedAt(null);
        userToReactivate.setDeletedByAdmin(null);
        // Cân nhắc cập nhật updatedAt nếu muốn:
        // userToReactivate.setUpdatedAt(Instant.now());

        // Vì userToReactivate được fetch bởi EntityManager, nó đã là một managed entity.
        // Các thay đổi sẽ được flush vào DB khi transaction commit.
        // Tuy nhiên, gọi save() rõ ràng cũng không sao và có thể rõ ràng hơn.
        userRepository.save(userToReactivate); // Hoặc chỉ để transaction tự commit

        logger.info("User {} reactivated successfully by admin {}", userId, adminUser.getEmail());
        return new ApiResponse(true, "User reactivated successfully.");
    }
}