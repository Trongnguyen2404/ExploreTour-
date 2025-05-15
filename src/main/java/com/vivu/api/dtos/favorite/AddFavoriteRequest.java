package com.vivu.api.dtos.favorite;

import com.vivu.api.enums.FavoriteType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddFavoriteRequest {
    @NotNull(message = "Favorite type cannot be null")
    private FavoriteType favoriteType; // TOUR or LOCATION

    @NotNull(message = "Favorite ID cannot be null")
    private Integer favoriteId;
}