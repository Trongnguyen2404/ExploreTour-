package com.vivu.api.dtos.favorite;

import com.vivu.api.dtos.location.LocationSummaryDto;
import com.vivu.api.dtos.tour.TourSummaryDto;
import com.vivu.api.enums.FavoriteType;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;


@Data
@Builder
public class FavoriteDto { // DTO để hiển thị danh sách yêu thích
    private Integer id; // ID của bản ghi favorite
    private FavoriteType favoriteType;
    private Integer favoriteId;
    private Instant createdAt;

    // Thông tin chi tiết của Tour hoặc Location được yêu thích
    private TourSummaryDto tour; // Sẽ là null nếu favoriteType là LOCATION
    private LocationSummaryDto location; // Sẽ là null nếu favoriteType là TOUR

    // Cần logic trong Service để gán tour hoặc location vào DTO này
}