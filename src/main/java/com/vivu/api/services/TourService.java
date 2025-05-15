package com.vivu.api.services;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.tour.TourDetailDto;
import com.vivu.api.dtos.tour.TourRequestDto; // Import DTO mới
import com.vivu.api.dtos.tour.TourSummaryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication; // Import Authentication

public interface TourService {
    // Public endpoints
    PagedResponse<TourSummaryDto> getAllTours(Pageable pageable);
    PagedResponse<TourSummaryDto> searchTours(String keyword, Pageable pageable);
    TourDetailDto getTourById(Integer tourId);

    // Admin endpoints
    TourDetailDto createTour(TourRequestDto requestDto, Authentication adminAuth);
    TourDetailDto updateTour(Integer tourId, TourRequestDto requestDto, Authentication adminAuth);
    ApiResponse deleteTour(Integer tourId, Authentication adminAuth);
}