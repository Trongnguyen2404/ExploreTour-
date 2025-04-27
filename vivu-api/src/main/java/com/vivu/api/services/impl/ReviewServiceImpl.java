package com.vivu.api.services.impl;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.review.CreateReviewRequest;
import com.vivu.api.dtos.review.ReviewDto;
import com.vivu.api.entities.Location;
import com.vivu.api.entities.Review;
import com.vivu.api.entities.Tour;
import com.vivu.api.entities.User;
import com.vivu.api.enums.TargetType;
import com.vivu.api.repositories.LocationRepository;
import com.vivu.api.repositories.ReviewRepository;
import com.vivu.api.repositories.TourRepository;
import com.vivu.api.repositories.UserRepository;
import com.vivu.api.security.services.UserDetailsImpl;
import com.vivu.api.services.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ReviewServiceImpl implements ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);


    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourRepository tourRepository; // Cần để cập nhật rating tour

    @Autowired
    private LocationRepository locationRepository; // Cần để cập nhật rating location

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDto> getReviewsForTarget(TargetType targetType, Integer targetId, Pageable pageable) {
        // Validate target existence (optional but good)
        validateTargetExists(targetType, targetId);

        Page<Review> reviewPage = reviewRepository.findByTargetTypeAndTargetId(targetType, targetId, pageable);
        return mapPageToPagedResponse(reviewPage, ReviewDto::fromEntity);
    }

    @Override
    @Transactional
    public ApiResponse createReview(CreateReviewRequest createReviewRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Kiểm tra xem target (Tour/Location) có tồn tại không
        validateTargetExists(createReviewRequest.getTargetType(), createReviewRequest.getTargetId());

        // Kiểm tra xem user đã review target này chưa
        Optional<Review> existingReview = reviewRepository.findByUserIdAndTargetTypeAndTargetId(
                currentUser.getId(),
                createReviewRequest.getTargetType(),
                createReviewRequest.getTargetId());

        if (existingReview.isPresent()) {
            return new ApiResponse(false, "You have already reviewed this item.");
        }

        // Tạo review mới
        Review review = Review.builder()
                .user(currentUser)
                .targetType(createReviewRequest.getTargetType())
                .targetId(createReviewRequest.getTargetId())
                .rating(createReviewRequest.getRating())
                .comment(createReviewRequest.getComment())
                .build();

        reviewRepository.save(review);
        logger.info("Review created successfully by user {} for {} ID {}",
                currentUser.getEmail(), review.getTargetType(), review.getTargetId());

        // Cập nhật rating trung bình cho target
        updateTargetAverageRating(review.getTargetType(), review.getTargetId());

        return new ApiResponse(true, "Review submitted successfully.");
    }

    @Override
    @Transactional
    public ApiResponse updateReview(Integer reviewId, CreateReviewRequest updateReviewRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));

        // Kiểm tra xem người dùng hiện tại có phải là người tạo review không
        if (!review.getUser().getId().equals(userDetails.getId())) {
            throw new AccessDeniedException("You are not allowed to update this review.");
        }

        // Kiểm tra target có trùng khớp không (không cho đổi target khi update)
        if (review.getTargetType() != updateReviewRequest.getTargetType() || !review.getTargetId().equals(updateReviewRequest.getTargetId())) {
            return new ApiResponse(false, "Cannot change the target of the review during update.");
        }

        // Cập nhật rating và comment
        review.setRating(updateReviewRequest.getRating());
        review.setComment(updateReviewRequest.getComment());
        reviewRepository.save(review);
        logger.info("Review updated successfully with id: {}", reviewId);

        // Cập nhật lại rating trung bình
        updateTargetAverageRating(review.getTargetType(), review.getTargetId());

        return new ApiResponse(true, "Review updated successfully.");
    }

    @Override
    @Transactional
    public ApiResponse deleteReview(Integer reviewId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));

        User currentUser = userRepository.findById(userDetails.getId()) // Lấy User object để kiểm tra role
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


        // Kiểm tra quyền xóa: chủ review hoặc admin
        if (!review.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != com.vivu.api.enums.Role.ADMIN) {
            throw new AccessDeniedException("You are not allowed to delete this review.");
        }

        TargetType targetType = review.getTargetType();
        Integer targetId = review.getTargetId();

        reviewRepository.delete(review);
        logger.info("Review deleted successfully with id: {}", reviewId);

        // Cập nhật lại rating trung bình sau khi xóa
        updateTargetAverageRating(targetType, targetId);

        return new ApiResponse(true, "Review deleted successfully.");
    }

    // --- Helper Methods ---

    private void validateTargetExists(TargetType targetType, Integer targetId) {
        boolean exists;
        if (targetType == TargetType.TOUR) {
            exists = tourRepository.existsById(targetId);
        } else { // LOCATION
            exists = locationRepository.existsById(targetId);
        }
        if (!exists) {
            throw new EntityNotFoundException(targetType + " not found with id: " + targetId);
        }
    }

    private void updateTargetAverageRating(TargetType targetType, Integer targetId) {
        Optional<Double> avgRatingOpt = reviewRepository.getAverageRatingForTarget(targetType, targetId);
        // Mặc định là 0 nếu không có review nào hoặc có lỗi tính toán
        BigDecimal newAverageRating = avgRatingOpt
                .map(avg -> BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);

        // long ratingCount = reviewRepository.countByTargetTypeAndTargetId(targetType, targetId); // Lấy count nếu cần lưu

        try {
            if (targetType == TargetType.TOUR) {
                Tour tour = tourRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Tour not found while updating rating: " + targetId));
                tour.setAverageRating(newAverageRating);
                // tour.setRatingCount(ratingCount); // Cập nhật count nếu có cột này
                tourRepository.save(tour);
                logger.debug("Updated average rating for Tour {} to {}", targetId, newAverageRating);
            } else { // LOCATION
                Location location = locationRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Location not found while updating rating: " + targetId));
                location.setAverageRating(newAverageRating);
                // location.setRatingCount(ratingCount);
                locationRepository.save(location);
                logger.debug("Updated average rating for Location {} to {}", targetId, newAverageRating);
            }
        } catch (EntityNotFoundException e) {
            logger.error("Error updating average rating: Target not found after review change. {}", e.getMessage());
            // Có thể không cần throw lại lỗi ở đây, chỉ log
        }

    }

    // Helper method để chuyển Page<Entity> thành PagedResponse<DTO>
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