package com.vivu.api.controllers;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.review.CreateReviewRequest;
import com.vivu.api.dtos.review.ReviewDto;
import com.vivu.api.enums.TargetType;
import com.vivu.api.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/reviews") // Base path cho review endpoints
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Endpoint lấy reviews cho một Tour hoặc Location cụ thể (Public)
    @GetMapping
    public ResponseEntity<PagedResponse<ReviewDto>> getReviews(
            @RequestParam("targetType") TargetType targetType,
            @RequestParam("targetId") Integer targetId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PagedResponse<ReviewDto> response = reviewService.getReviewsForTarget(targetType, targetId, pageable);
        return ResponseEntity.ok(response);
    }

    // Endpoint tạo review mới (yêu cầu xác thực)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> createReview(@Valid @RequestBody CreateReviewRequest createReviewRequest, Authentication authentication) {
        ApiResponse response = reviewService.createReview(createReviewRequest, authentication);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Endpoint cập nhật review (yêu cầu xác thực và là chủ review)
    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> updateReview(
            @PathVariable Integer reviewId,
            @Valid @RequestBody CreateReviewRequest updateReviewRequest,
            Authentication authentication) {
        ApiResponse response = reviewService.updateReview(reviewId, updateReviewRequest, authentication);
        // Service đã kiểm tra quyền sở hữu
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Endpoint xóa review (yêu cầu xác thực - quyền xóa được kiểm tra trong service)
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Integer reviewId, Authentication authentication) {
        // Service sẽ kiểm tra user hiện tại là chủ review hoặc là Admin
        ApiResponse response = reviewService.deleteReview(reviewId, authentication);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response); // Có thể là 403 Forbidden nếu AccessDeniedException được xử lý
        }
    }
}