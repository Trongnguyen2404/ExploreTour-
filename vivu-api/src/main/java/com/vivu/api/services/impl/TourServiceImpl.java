package com.vivu.api.services.impl;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.tour.TourDetailDto;
import com.vivu.api.dtos.tour.TourRequestDto;
import com.vivu.api.dtos.tour.TourSummaryDto;
import com.vivu.api.entities.Tour;
import com.vivu.api.entities.User;
import com.vivu.api.repositories.TourRepository;
import com.vivu.api.repositories.UserRepository;
import com.vivu.api.security.services.UserDetailsImpl;
import com.vivu.api.services.TourService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourServiceImpl implements TourService {

    private static final Logger logger = LoggerFactory.getLogger(TourServiceImpl.class);

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TourSummaryDto> getAllTours(Pageable pageable) {
        Page<Tour> tourPage = tourRepository.findAll(pageable);
        return mapPageToPagedResponse(tourPage, TourSummaryDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TourSummaryDto> searchTours(String keyword, Pageable pageable) {
        Page<Tour> tourPage = tourRepository.searchTours(keyword, pageable);
        return mapPageToPagedResponse(tourPage, TourSummaryDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public TourDetailDto getTourById(Integer tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id: " + tourId));
        TourDetailDto dto = TourDetailDto.fromEntity(tour);
        return dto;
    }

    @Override
    @Transactional
    public TourDetailDto createTour(TourRequestDto requestDto, Authentication adminAuth) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) adminAuth.getPrincipal();
        User adminUser = userRepository.findById(adminDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        if (requestDto.getTourCode() != null && !requestDto.getTourCode().isBlank()) {
            tourRepository.findByTourCode(requestDto.getTourCode()).ifPresent(t -> {
                throw new IllegalArgumentException("Tour code already exists: " + requestDto.getTourCode());
            });
        }

        Tour tour = Tour.builder()
                .title(requestDto.getTitle())
                .mainImageUrl(requestDto.getMainImageUrl())
                .locationName(requestDto.getLocationName())
                .itineraryDuration(requestDto.getItineraryDuration())
                .departureDate(requestDto.getDepartureDate())
                .availableSlots(requestDto.getAvailableSlots())
                .averageRating(BigDecimal.ZERO)
                .tourCode(requestDto.getTourCode())
                .contactPhone(requestDto.getContactPhone())
                .content(requestDto.getContent())
                .scheduleImageUrl(requestDto.getScheduleImageUrl())
                .createdByAdmin(adminUser)
                .updatedByAdmin(adminUser)
                .build();

        Tour savedTour = tourRepository.save(tour);
        logger.info("Tour created successfully with ID {} by admin {}", savedTour.getId(), adminUser.getEmail());
        return TourDetailDto.fromEntity(savedTour);
    }

    @Override
    @Transactional
    public TourDetailDto updateTour(Integer tourId, TourRequestDto requestDto, Authentication adminAuth) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) adminAuth.getPrincipal();
        User adminUser = userRepository.findById(adminDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id: " + tourId));

        if (requestDto.getTourCode() != null && !requestDto.getTourCode().isBlank() && !requestDto.getTourCode().equals(tour.getTourCode())) {
            tourRepository.findByTourCode(requestDto.getTourCode()).ifPresent(t -> {
                throw new IllegalArgumentException("Tour code already exists: " + requestDto.getTourCode());
            });
        }

        tour.setTitle(requestDto.getTitle());
        tour.setMainImageUrl(requestDto.getMainImageUrl());
        tour.setLocationName(requestDto.getLocationName());
        tour.setItineraryDuration(requestDto.getItineraryDuration());
        tour.setDepartureDate(requestDto.getDepartureDate());
        tour.setAvailableSlots(requestDto.getAvailableSlots());
        tour.setTourCode(requestDto.getTourCode());
        tour.setContactPhone(requestDto.getContactPhone());
        tour.setContent(requestDto.getContent());
        tour.setScheduleImageUrl(requestDto.getScheduleImageUrl());
        tour.setUpdatedByAdmin(adminUser);

        Tour updatedTour = tourRepository.save(tour);
        logger.info("Tour updated successfully with ID {} by admin {}", updatedTour.getId(), adminUser.getEmail());
        return TourDetailDto.fromEntity(updatedTour);
    }

    @Override
    @Transactional
    public ApiResponse deleteTour(Integer tourId, Authentication adminAuth) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) adminAuth.getPrincipal();

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id: " + tourId));

        tourRepository.delete(tour);
        logger.info("Tour with ID {} deleted successfully by admin {}", tourId, adminDetails.getEmail());
        return new ApiResponse(true, "Tour deleted successfully.");
    }

    private <T, U> PagedResponse<U> mapPageToPagedResponse(Page<T> page, java.util.function.Function<T, U> mapper) {
        List<U> dtoList = page.getContent().stream().map(mapper).collect(Collectors.toList());
        return new PagedResponse<>(
                dtoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}