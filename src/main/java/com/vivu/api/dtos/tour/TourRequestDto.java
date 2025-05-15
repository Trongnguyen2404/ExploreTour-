package com.vivu.api.dtos.tour;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TourRequestDto { // DTO dùng chung cho cả Create và Update

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Main image URL cannot be blank")
    @Size(max = 512)
    private String mainImageUrl;

    @NotBlank(message = "Location name cannot be blank")
    @Size(max = 100)
    private String locationName;

    @NotBlank(message = "Itinerary duration cannot be blank")
    @Size(max = 50)
    private String itineraryDuration; // VD: 4D3N

    private LocalDate departureDate; // Có thể null

    @NotNull(message = "Available slots cannot be null")
    @Min(value = 0, message = "Available slots must be non-negative")
    private Integer availableSlots;

// Average rating không cần nhập, sẽ được tính toán
// private BigDecimal averageRating;

    @Size(max = 50, message = "Tour code must be less than 50 characters")
    private String tourCode; // Có thể null

    @Size(max = 20, message = "Contact phone must be less than 20 characters")
    private String contactPhone; // Có thể null

    private String content; // Mô tả chi tiết, có thể null

    @Size(max = 512, message = "Schedule image URL is too long")
    private String scheduleImageUrl; // Có thể null
}