package com.vivu.api.dtos.review;

import com.vivu.api.enums.TargetType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReviewRequest {
    @NotNull(message = "Target type cannot be null")
    private TargetType targetType; // TOUR or LOCATION

    @NotNull(message = "Target ID cannot be null")
    private Integer targetId;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Byte rating;

    private String comment; // Có thể null hoặc trống
}