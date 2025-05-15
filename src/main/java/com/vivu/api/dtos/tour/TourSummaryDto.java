package com.vivu.api.dtos.tour;

import com.vivu.api.entities.Tour;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TourSummaryDto {
    private Integer id;
    private String title;
    private String mainImageUrl;
    private String locationName;
    private String itineraryDuration; // VD: 4D3N
    private LocalDate departureDate;
    private Integer availableSlots;
    private BigDecimal averageRating;

    public static TourSummaryDto fromEntity(Tour tour) {
        if (tour == null) return null;
        return TourSummaryDto.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .mainImageUrl(tour.getMainImageUrl())
                .locationName(tour.getLocationName())
                .itineraryDuration(tour.getItineraryDuration())
                .departureDate(tour.getDepartureDate())
                .availableSlots(tour.getAvailableSlots())
                .averageRating(tour.getAverageRating())
                .build();
    }
}