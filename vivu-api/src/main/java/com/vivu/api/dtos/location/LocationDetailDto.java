package com.vivu.api.dtos.location;

import com.vivu.api.dtos.review.ReviewDto; // Sẽ tạo sau
import com.vivu.api.entities.Location;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
public class LocationDetailDto {
    // Thông tin cơ bản
    private Integer id;
    private String title;
    private String headerImageUrl;
    private BigDecimal averageRating;

    // Danh sách các khối nội dung
    private List<LocationContentBlockDto> contentBlocks;

    // Danh sách reviews nếu cần
     private List<ReviewDto> reviews;

    public static LocationDetailDto fromEntity(Location location) {
        if (location == null) return null;
        return LocationDetailDto.builder()
                .id(location.getId())
                .title(location.getTitle())
                .headerImageUrl(location.getHeaderImageUrl())
                .averageRating(location.getAverageRating())
                .contentBlocks(location.getContentBlocks() != null ? // Check null trước khi stream
                        location.getContentBlocks().stream()
                                .map(LocationContentBlockDto::fromEntity)
                                .collect(Collectors.toList()) : List.of())
                .build();
    }
}