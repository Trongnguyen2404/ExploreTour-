package com.vivu.api.controllers;

import com.vivu.api.dtos.common.ApiResponse; // Import ApiResponse
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.tour.TourDetailDto;
import com.vivu.api.dtos.tour.TourRequestDto; // Import TourRequestDto
import com.vivu.api.dtos.tour.TourSummaryDto;
import com.vivu.api.services.TourService;
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
@RequestMapping("/api/v1/tours") // Base path cho tour endpoints
public class TourController {

    @Autowired
    private TourService tourService;

// --- Public endpoints ---

    @GetMapping
    public ResponseEntity<PagedResponse<TourSummaryDto>> getTours(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PagedResponse<TourSummaryDto> response;
        if (search != null && !search.isBlank()) {
            response = tourService.searchTours(search, pageable);
        } else {
            response = tourService.getAllTours(pageable);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<TourDetailDto> getTourById(@PathVariable Integer tourId) {
        TourDetailDto tourDetail = tourService.getTourById(tourId);
        return ResponseEntity.ok(tourDetail);
    }

// --- Admin endpoints ---

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được tạo tour
    public ResponseEntity<TourDetailDto> createTour(
            @Valid @RequestBody TourRequestDto requestDto,
            Authentication authentication) { // Lấy thông tin admin từ Authentication
        TourDetailDto createdTour = tourService.createTour(requestDto, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTour); // Trả về 201 Created
    }

    @PutMapping("/{tourId}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được sửa tour
    public ResponseEntity<TourDetailDto> updateTour(
            @PathVariable Integer tourId,
            @Valid @RequestBody TourRequestDto requestDto,
            Authentication authentication) {
        TourDetailDto updatedTour = tourService.updateTour(tourId, requestDto, authentication);
        return ResponseEntity.ok(updatedTour);
    }

    @DeleteMapping("/{tourId}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được xóa tour
    public ResponseEntity<ApiResponse> deleteTour(
            @PathVariable Integer tourId,
            Authentication authentication) {
        ApiResponse response = tourService.deleteTour(tourId, authentication);
        return ResponseEntity.ok(response);
    }
}