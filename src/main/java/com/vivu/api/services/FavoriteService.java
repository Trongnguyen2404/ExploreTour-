package com.vivu.api.services;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.favorite.AddFavoriteRequest;
import com.vivu.api.dtos.favorite.FavoriteDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface FavoriteService {
    PagedResponse<FavoriteDto> getUserFavorites(Pageable pageable, Authentication authentication);
    ApiResponse addFavorite(AddFavoriteRequest addFavoriteRequest, Authentication authentication);
    ApiResponse removeFavorite(AddFavoriteRequest removeFavoriteRequest, Authentication authentication); // Hoặc chỉ cần favoriteId/type và user
}