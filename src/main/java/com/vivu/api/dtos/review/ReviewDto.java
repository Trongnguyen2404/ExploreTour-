package com.vivu.api.dtos.review;

import com.vivu.api.dtos.user.UserSummaryDto; // Sẽ tạo DTO này
import com.vivu.api.entities.Review;
import com.vivu.api.enums.TargetType;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;


@Data
@Builder
public class ReviewDto {
    private Integer id;
    private UserSummaryDto user; // Thông tin người đánh giá
    private TargetType targetType;
    private Integer targetId;
    private Byte rating;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;

    // Cần có UserSummaryDto để hiển thị thông tin user trong review
    // Phương thức fromEntity sẽ cần join hoặc lấy User từ Review entity
    public static ReviewDto fromEntity(Review review) {
        if (review == null) return null;
        return ReviewDto.builder()
                .id(review.getId())
                // Tạo UserSummaryDto từ review.getUser()
                .user(UserSummaryDto.fromEntity(review.getUser()))
                .targetType(review.getTargetType())
                .targetId(review.getTargetId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}