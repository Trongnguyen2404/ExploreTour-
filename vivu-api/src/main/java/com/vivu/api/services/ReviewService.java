package com.vivu.api.services;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.review.CreateReviewRequest;
import com.vivu.api.dtos.review.ReviewDto;
import com.vivu.api.enums.TargetType;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ReviewService {
    PagedResponse<ReviewDto> getReviewsForTarget(TargetType targetType, Integer targetId, Pageable pageable);
    ApiResponse createReview(CreateReviewRequest createReviewRequest, Authentication authentication);
    ApiResponse updateReview(Integer reviewId, CreateReviewRequest updateReviewRequest, Authentication authentication); // Giả sử dùng lại DTO tạo để sửa
    ApiResponse deleteReview(Integer reviewId, Authentication authentication);
}