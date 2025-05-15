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
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found with ID: " + adminDetails.getId()));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + locationId));

        // Cập nhật các thuộc tính cơ bản của Location
        location.setTitle(requestDto.getTitle());
        location.setHeaderImageUrl(requestDto.getHeaderImageUrl());
        location.setUpdatedByAdmin(adminUser); // Ghi nhận người cập nhật

        // --- Xử lý LocationContentBlocks ---

        // 1. Xóa các content block cũ của location này trực tiếp từ DB
        // Điều này đảm bảo các bản ghi cũ thực sự bị xóa khỏi DB.
        locationContentBlockRepository.deleteByLocationId(locationId);
        locationContentBlockRepository.flush(); // QUAN TRỌNG: Đẩy lệnh DELETE ra DB ngay lập tức

        // 2. Lấy và làm rỗng collection contentBlocks hiện tại trong entity Location.
        // Bước này cần thiết để Hibernate biết rằng các entity con (nếu có trong context)
        // đã được gỡ bỏ khỏi collection của cha, và orphanRemoval (nếu được cấu hình đúng
        // và nếu các block chưa bị xóa bởi deleteByLocationId) sẽ xử lý chúng.
        // Đồng thời, nó chuẩn bị collection để nhận các block mới.
        List<LocationContentBlock> managedContentBlocks = location.getContentBlocks();
        if (managedContentBlocks == null) {
            // Trường hợp hiếm khi collection là null sau khi load entity,
            // nhưng để an toàn, hãy khởi tạo nếu cần.
            managedContentBlocks = new java.util.ArrayList<>(); // Sử dụng java.util.ArrayList
            location.setContentBlocks(managedContentBlocks); // Gán collection mới (rỗng) để Hibernate quản lý
        } else {
            // Nếu collection đã tồn tại, chỉ cần làm rỗng nó.
            // Các entity con bị gỡ bỏ sẽ được xử lý bởi orphanRemoval=true khi flush/commit.
            managedContentBlocks.clear();
        }

        // 3. Sắp xếp các block mới từ DTO
        requestDto.getContentBlocks().sort(Comparator.comparing(LocationContentBlockRequestDto::getOrderIndex));

        // 4. Tạo các entity LocationContentBlock mới từ DTO
        List<LocationContentBlock> newContentBlockEntities = requestDto.getContentBlocks().stream()
                .map(blockDto -> LocationContentBlock.builder()
                        .location(location) // QUAN TRỌNG: Liên kết với Location cha
                        .orderIndex(blockDto.getOrderIndex())
                        .blockType(blockDto.getBlockType())
                        .contentValue(blockDto.getContentValue())
                        .caption(blockDto.getCaption())
                        .build()) // Các entity này chưa có ID và chưa được persist
                .collect(Collectors.toList());

        // 5. Lưu các entity LocationContentBlock mới này vào DB để chúng có ID và được quản lý.
        // Bước này sẽ thực hiện các lệnh INSERT.
        List<LocationContentBlock> savedNewBlocks = locationContentBlockRepository.saveAll(newContentBlockEntities);

        // 6. Thêm các LocationContentBlock mới (đã được lưu và có ID) vào collection
        // 'managedContentBlocks' của entity Location.
        // KHÔNG được gán lại `location.setContentBlocks(savedNewBlocks)` vì `managedContentBlocks`
        // chính là tham chiếu đến `location.getContentBlocks()`.
        // Việc này đảm bảo Hibernate biết rằng các entity con này thuộc về `location`.
        managedContentBlocks.addAll(savedNewBlocks);
        // Sau bước này, `location.getContentBlocks()` sẽ chứa các block mới đã được lưu.

        // 7. Lưu entity Location cha.
        // Do `cascade = CascadeType.ALL` (hoặc tương đương) trên `Location.contentBlocks`,
        // Hibernate sẽ tự động quản lý việc liên kết các `savedNewBlocks` với `location`
        // trong DB (nếu chưa được thực hiện hoàn toàn bởi `saveAll` và thiết lập `.location(location)`).
        // Các block cũ đã được clear khỏi collection và/hoặc xóa bởi deleteByLocationId.
        Location updatedLocation = locationRepository.save(location);

        logger.info("Location updated successfully with ID {} by admin {}", updatedLocation.getId(), adminUser.getEmail());

        // 8. Fetch lại entity Location từ DB để đảm bảo nhận được state mới nhất,
        // bao gồm cả các contentBlocks đã được liên kết đúng và có ID đầy đủ.
        // Mặc dù `updatedLocation` đã là entity được quản lý, việc fetch lại
        // là một thói quen tốt để đảm bảo dữ liệu trả về là chính xác nhất.
        Location fetchedLocation = locationRepository.findById(updatedLocation.getId())
                .orElse(updatedLocation); // Fallback trong trường hợp không tìm thấy (hiếm)

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