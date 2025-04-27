package com.vivu.api.services.impl;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.dtos.common.PagedResponse;
import com.vivu.api.dtos.location.LocationContentBlockRequestDto;
import com.vivu.api.dtos.location.LocationDetailDto;
import com.vivu.api.dtos.location.LocationRequestDto;
import com.vivu.api.dtos.location.LocationSummaryDto;
import com.vivu.api.entities.Location;
import com.vivu.api.entities.LocationContentBlock;
import com.vivu.api.entities.User;
import com.vivu.api.repositories.LocationContentBlockRepository;
import com.vivu.api.repositories.LocationRepository;
import com.vivu.api.repositories.UserRepository;
import com.vivu.api.security.services.UserDetailsImpl;
import com.vivu.api.services.LocationService;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationContentBlockRepository locationContentBlockRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<LocationSummaryDto> getAllLocations(Pageable pageable) {
        Page<Location> locationPage = locationRepository.findAll(pageable);
        return mapPageToPagedResponse(locationPage, LocationSummaryDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<LocationSummaryDto> searchLocations(String keyword, Pageable pageable) {
        Page<Location> locationPage = locationRepository.searchLocations(keyword, pageable);
        return mapPageToPagedResponse(locationPage, LocationSummaryDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationDetailDto getLocationById(Integer locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + locationId));
        LocationDetailDto dto = LocationDetailDto.fromEntity(location);
        return dto;
    }

    @Override
    @Transactional
    public LocationDetailDto createLocation(LocationRequestDto requestDto, Authentication adminAuth) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) adminAuth.getPrincipal();
        User adminUser = userRepository.findById(adminDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        Location location = Location.builder()
                .title(requestDto.getTitle())
                .headerImageUrl(requestDto.getHeaderImageUrl())
                .averageRating(BigDecimal.ZERO)
                .createdByAdmin(adminUser)
                .updatedByAdmin(adminUser)
                .build();
        Location savedLocation = locationRepository.save(location);

        requestDto.getContentBlocks().sort(Comparator.comparing(LocationContentBlockRequestDto::getOrderIndex));

        List<LocationContentBlock> contentBlocks = requestDto.getContentBlocks().stream()
                .map(blockDto -> LocationContentBlock.builder()
                        .location(savedLocation)
                        .orderIndex(blockDto.getOrderIndex())
                        .blockType(blockDto.getBlockType())
                        .contentValue(blockDto.getContentValue())
                        .caption(blockDto.getCaption())
                        .build())
                .collect(Collectors.toList());

        locationContentBlockRepository.saveAll(contentBlocks);
        savedLocation.setContentBlocks(contentBlocks);

        logger.info("Location created successfully with ID {} by admin {}", savedLocation.getId(), adminUser.getEmail());
        Location fetchedLocation = locationRepository.findById(savedLocation.getId()).orElse(savedLocation);
        return LocationDetailDto.fromEntity(fetchedLocation);
    }

    @Override
    @Transactional
    public LocationDetailDto updateLocation(Integer locationId, LocationRequestDto requestDto, Authentication adminAuth) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) adminAuth.getPrincipal();
        User adminUser = userRepository.findById(adminDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + locationId));

        location.setTitle(requestDto.getTitle());
        location.setHeaderImageUrl(requestDto.getHeaderImageUrl());
        location.setUpdatedByAdmin(adminUser);

        locationContentBlockRepository.deleteByLocationId(locationId);

        requestDto.getContentBlocks().sort(Comparator.comparing(LocationContentBlockRequestDto::getOrderIndex));

        List<LocationContentBlock> newContentBlocks = requestDto.getContentBlocks().stream()
                .map(blockDto -> LocationContentBlock.builder()
                        .location(location)
                        .orderIndex(blockDto.getOrderIndex())
                        .blockType(blockDto.getBlockType())
                        .contentValue(blockDto.getContentValue())
                        .caption(blockDto.getCaption())
                        .build())
                .collect(Collectors.toList());

        locationContentBlockRepository.saveAll(newContentBlocks);
        location.setContentBlocks(newContentBlocks);

        Location updatedLocation = locationRepository.save(location);

        logger.info("Location updated successfully with ID {} by admin {}", updatedLocation.getId(), adminUser.getEmail());
        Location fetchedLocation = locationRepository.findById(updatedLocation.getId()).orElse(updatedLocation);
        return LocationDetailDto.fromEntity(fetchedLocation);
    }

    @Override
    @Transactional
    public ApiResponse deleteLocation(Integer locationId, Authentication adminAuth) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) adminAuth.getPrincipal();

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + locationId));

        locationRepository.delete(location);
        logger.info("Location with ID {} deleted successfully by admin {}", locationId, adminDetails.getEmail());
        return new ApiResponse(true, "Location deleted successfully.");
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