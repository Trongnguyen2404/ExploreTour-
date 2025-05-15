package com.vivu.api.controllers;

import com.vivu.api.dtos.common.ApiResponse; // Import ApiResponse
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.location.LocationDetailDto;
import com.vivu.api.dtos.location.LocationRequestDto; // Import LocationRequestDto
import com.vivu.api.dtos.location.LocationSummaryDto;
import com.vivu.api.services.LocationService;
import jakarta.validation.Valid; // Import Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import PreAuthorize
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/locations") // Base path cho location endpoints
public class LocationController {

    @Autowired
    private LocationService locationService;

// --- Public endpoints ---

    @GetMapping
    public ResponseEntity<PagedResponse<LocationSummaryDto>> getLocations(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PagedResponse<LocationSummaryDto> response;
        if (search != null && !search.isBlank()) {
            response = locationService.searchLocations(search, pageable);
        } else {
            response = locationService.getAllLocations(pageable);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationDetailDto> getLocationById(@PathVariable Integer locationId) {
        LocationDetailDto locationDetail = locationService.getLocationById(locationId);
        return ResponseEntity.ok(locationDetail);
    }

// --- Admin endpoints ---

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được tạo location
    public ResponseEntity<LocationDetailDto> createLocation(
            @Valid @RequestBody LocationRequestDto requestDto,
            Authentication authentication) {
        LocationDetailDto createdLocation = locationService.createLocation(requestDto, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation); // Trả về 201 Created
    }

    @PutMapping("/{locationId}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được sửa location
    public ResponseEntity<LocationDetailDto> updateLocation(
            @PathVariable Integer locationId,
            @Valid @RequestBody LocationRequestDto requestDto,
            Authentication authentication) {
        LocationDetailDto updatedLocation = locationService.updateLocation(locationId, requestDto, authentication);
        return ResponseEntity.ok(updatedLocation);
    }

    @DeleteMapping("/{locationId}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được xóa location
    public ResponseEntity<ApiResponse> deleteLocation(
            @PathVariable Integer locationId,
            Authentication authentication) {
        ApiResponse response = locationService.deleteLocation(locationId, authentication);
        return ResponseEntity.ok(response);
    }

}