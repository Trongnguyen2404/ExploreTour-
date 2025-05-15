package com.vivu.api.controllers;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.favorite.AddFavoriteRequest;
import com.vivu.api.dtos.favorite.FavoriteDto;
import com.vivu.api.services.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/favorites") // Base path cho favorite endpoints
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // Endpoint lấy danh sách yêu thích của user hiện tại (yêu cầu xác thực)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<FavoriteDto>> getUserFavorites(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            Authentication authentication
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PagedResponse<FavoriteDto> response = favoriteService.getUserFavorites(pageable, authentication);
        return ResponseEntity.ok(response);
    }

    // Endpoint thêm một mục vào yêu thích (yêu cầu xác thực)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> addFavorite(@Valid @RequestBody AddFavoriteRequest addFavoriteRequest, Authentication authentication) {
        ApiResponse response = favoriteService.addFavorite(addFavoriteRequest, authentication);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response); // Hoặc CREATED
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Endpoint xóa một mục khỏi yêu thích (yêu cầu xác thực)
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> removeFavorite(@Valid @RequestBody AddFavoriteRequest removeFavoriteRequest, Authentication authentication) {
        ApiResponse response = favoriteService.removeFavorite(removeFavoriteRequest, authentication);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}