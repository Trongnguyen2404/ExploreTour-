package com.vivu.api.dtos.location;

import com.vivu.api.entities.Location;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LocationSummaryDto {
    private Integer id;
    private String title;
    private String headerImageUrl;
    private BigDecimal averageRating;

    public static LocationSummaryDto fromEntity(Location location) {
        if (location == null) return null;
        return LocationSummaryDto.builder()
                .id(location.getId())
                .title(location.getTitle())
                .headerImageUrl(location.getHeaderImageUrl())
                .averageRating(location.getAverageRating())
                .build();
    }
}