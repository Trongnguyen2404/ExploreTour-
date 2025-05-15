package com.vivu.api.services;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.location.LocationDetailDto;
import com.vivu.api.dtos.location.LocationRequestDto; // Import DTO mới
import com.vivu.api.dtos.location.LocationSummaryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication; // Import Authentication

public interface LocationService {
    // Public endpoints
    PagedResponse<LocationSummaryDto> getAllLocations(Pageable pageable);
    PagedResponse<LocationSummaryDto> searchLocations(String keyword, Pageable pageable);
    LocationDetailDto getLocationById(Integer locationId);

    // Admin endpoints
    LocationDetailDto createLocation(LocationRequestDto requestDto, Authentication adminAuth);
    LocationDetailDto updateLocation(Integer locationId, LocationRequestDto requestDto, Authentication adminAuth);
    ApiResponse deleteLocation(Integer locationId, Authentication adminAuth);
}