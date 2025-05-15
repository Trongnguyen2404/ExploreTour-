package com.vivu.api.dtos.tour;

import com.vivu.api.dtos.review.ReviewDto; // Sẽ tạo sau
import com.vivu.api.entities.Tour;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TourDetailDto {
    // Kế thừa hoặc chứa thông tin từ TourSummaryDto
    private Integer id;
    private String title;
    private String mainImageUrl;
    private String locationName;
    private String itineraryDuration;
    private LocalDate departureDate;
    private Integer availableSlots;
    private BigDecimal averageRating;

    // Thông tin chi tiết thêm
    private String tourCode;
    private String contactPhone;
    private String content; // Mô tả chi tiết, lịch trình
    private String scheduleImageUrl;

    // Có thể thêm danh sách reviews nếu cần
     private List<ReviewDto> reviews;

    // Có thể thêm thông tin admin tạo/sửa nếu cần
     private Integer createdByAdminId;
     private Integer updatedByAdminId;

    public static TourDetailDto fromEntity(Tour tour) {
        if (tour == null) return null;
        return TourDetailDto.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .mainImageUrl(tour.getMainImageUrl())
                .locationName(tour.getLocationName())
                .itineraryDuration(tour.getItineraryDuration())
                .departureDate(tour.getDepartureDate())
                .availableSlots(tour.getAvailableSlots())
                .averageRating(tour.getAverageRating())
                .tourCode(tour.getTourCode())
                .contactPhone(tour.getContactPhone())
                .content(tour.getContent())
                .scheduleImageUrl(tour.getScheduleImageUrl())
                 .createdByAdminId(tour.getCreatedByAdmin() != null ? tour.getCreatedByAdmin().getId() : null)
                 .updatedByAdminId(tour.getUpdatedByAdmin() != null ? tour.getUpdatedByAdmin().getId() : null)
                .build();
    }
}