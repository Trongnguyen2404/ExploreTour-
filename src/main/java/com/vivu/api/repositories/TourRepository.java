package com.vivu.api.repositories;

import com.vivu.api.entities.Tour;
import com.vivu.api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Thêm import này
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Thêm import này

import java.math.BigDecimal;
import java.util.Optional; // Thêm import này

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {

    // Tìm kiếm tour theo title hoặc location name (không phân biệt hoa thường)
// Sử dụng Pageable để phân trang
    @Query("SELECT t FROM Tour t WHERE LOWER(t.title) LIKE LOWER(concat('%', :keyword, '%')) OR LOWER(t.locationName) LIKE LOWER(concat('%', :keyword, '%'))")
    Page<Tour> searchTours(@Param("keyword") String keyword, Pageable pageable);

// Lấy tất cả tour với phân trang (sẽ bị ảnh hưởng bởi @Where nếu có trong Tour entity)
// Page<Tour> findAll(Pageable pageable); // Đã có sẵn từ JpaRepository

    // Tìm tour theo tour_code (cần cho chức năng Admin)
    Optional<Tour> findByTourCode(String tourCode);

    // Cập nhật average rating (ví dụ, có thể gọi từ service)
    @Modifying // Cần thiết cho UPDATE/DELETE query
    @Transactional // Cần thiết cho UPDATE/DELETE query
    @Query("UPDATE Tour t SET t.averageRating = :newRating WHERE t.id = :tourId")
    int updateAverageRating(@Param("tourId") Integer tourId, @Param("newRating") BigDecimal newRating); // Trả về int (số dòng bị ảnh hưởng)
    @Modifying
    @Transactional
    @Query("UPDATE Tour t SET t.createdByAdmin = NULL WHERE t.createdByAdmin = :adminUser")
    void nullifyCreatedByAdmin(@Param("adminUser") User adminUser);

    @Modifying
    @Transactional
    @Query("UPDATE Tour t SET t.updatedByAdmin = NULL WHERE t.updatedByAdmin = :adminUser")
    void nullifyUpdatedByAdmin(@Param("adminUser") User adminUser);
}