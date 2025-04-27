package com.vivu.api.services.impl;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.favorite.AddFavoriteRequest;
import com.vivu.api.dtos.favorite.FavoriteDto;
import com.vivu.api.dtos.location.LocationSummaryDto;
import com.vivu.api.dtos.tour.TourSummaryDto;
import com.vivu.api.entities.Favorite;
import com.vivu.api.entities.Location;
import com.vivu.api.entities.Tour;
import com.vivu.api.entities.User;
import com.vivu.api.enums.FavoriteType;
import com.vivu.api.repositories.FavoriteRepository;
import com.vivu.api.repositories.LocationRepository;
import com.vivu.api.repositories.TourRepository;
import com.vivu.api.repositories.UserRepository;
import com.vivu.api.security.services.UserDetailsImpl;
import com.vivu.api.services.FavoriteService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    private static final Logger logger = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private LocationRepository locationRepository;


    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FavoriteDto> getUserFavorites(Pageable pageable, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Page<Favorite> favoritePage = favoriteRepository.findByUserId(userDetails.getId(), pageable);

        // Map Favorite entity sang FavoriteDto và lấy thông tin Tour/Location tương ứng
        List<FavoriteDto> favoriteDtos = new ArrayList<>();
        for (Favorite fav : favoritePage.getContent()) {
            FavoriteDto dto = FavoriteDto.builder()
                    .id(fav.getId())
                    .favoriteType(fav.getFavoriteType())
                    .favoriteId(fav.getFavoriteId())
                    .createdAt(fav.getCreatedAt())
                    .build();

            if (fav.getFavoriteType() == FavoriteType.TOUR) {
                tourRepository.findById(fav.getFavoriteId())
                        .ifPresent(tour -> dto.setTour(TourSummaryDto.fromEntity(tour)));
            } else { // LOCATION
                locationRepository.findById(fav.getFavoriteId())
                        .ifPresent(loc -> dto.setLocation(LocationSummaryDto.fromEntity(loc)));
            }
            favoriteDtos.add(dto);
        }


        return new PagedResponse<>(
                favoriteDtos,
                favoritePage.getNumber(),
                favoritePage.getSize(),
                favoritePage.getTotalElements(),
                favoritePage.getTotalPages(),
                favoritePage.isLast()
        );
    }

    @Override
    @Transactional
    public ApiResponse addFavorite(AddFavoriteRequest addFavoriteRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Kiểm tra xem target (Tour/Location) có tồn tại không
        validateTargetExists(addFavoriteRequest.getFavoriteType(), addFavoriteRequest.getFavoriteId());

        // Kiểm tra xem đã yêu thích chưa
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndFavoriteTypeAndFavoriteId(
                currentUser.getId(),
                addFavoriteRequest.getFavoriteType(),
                addFavoriteRequest.getFavoriteId()
        );

        if (existingFavorite.isPresent()) {
            // Đã tồn tại, không cần làm gì hoặc trả về thông báo khác
            return new ApiResponse(true, "Item is already in favorites.");
        }

        // Tạo bản ghi favorite mới
        Favorite favorite = Favorite.builder()
                .user(currentUser)
                .favoriteType(addFavoriteRequest.getFavoriteType())
                .favoriteId(addFavoriteRequest.getFavoriteId())
                .build();

        favoriteRepository.save(favorite);
        logger.info("Item {} ID {} added to favorites for user {}",
                favorite.getFavoriteType(), favorite.getFavoriteId(), currentUser.getEmail());

        return new ApiResponse(true, "Item added to favorites.");
    }

    @Override
    @Transactional
    public ApiResponse removeFavorite(AddFavoriteRequest removeFavoriteRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Tìm và xóa trực tiếp bằng query method
        long deletedCount = favoriteRepository.deleteByUserIdAndFavoriteTypeAndFavoriteId(
                userDetails.getId(),
                removeFavoriteRequest.getFavoriteType(),
                removeFavoriteRequest.getFavoriteId()
        );

        if (deletedCount > 0) {
            logger.info("Item {} ID {} removed from favorites for user {}",
                    removeFavoriteRequest.getFavoriteType(), removeFavoriteRequest.getFavoriteId(), userDetails.getEmail());
            return new ApiResponse(true, "Item removed from favorites.");
        } else {
            // Không tìm thấy bản ghi để xóa
            logger.warn("Attempted to remove non-existent favorite {} ID {} for user {}",
                    removeFavoriteRequest.getFavoriteType(), removeFavoriteRequest.getFavoriteId(), userDetails.getEmail());
            return new ApiResponse(false, "Item not found in favorites.");
        }
    }

    // Helper method để kiểm tra Tour/Location tồn tại
    private void validateTargetExists(FavoriteType favoriteType, Integer favoriteId) {
        boolean exists;
        if (favoriteType == FavoriteType.TOUR) {
            exists = tourRepository.existsById(favoriteId);
        } else { // LOCATION
            exists = locationRepository.existsById(favoriteId);
        }
        if (!exists) {
            throw new EntityNotFoundException(favoriteType + " not found with id: " + favoriteId);
        }
    }

}