package com.vivu.api.repositories;

import com.vivu.api.entities.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Thêm import này
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Thêm import này

import java.math.BigDecimal;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    // Tìm kiếm location theo title (không phân biệt hoa thường) với phân trang
    @Query("SELECT l FROM Location l WHERE LOWER(l.title) LIKE LOWER(concat('%', :keyword, '%'))")
    Page<Location> searchLocations(@Param("keyword") String keyword, Pageable pageable);

    // Lấy tất cả location với phân trang (bị ảnh hưởng bởi @Where nếu có)
    // Page<Location> findAll(Pageable pageable); // Có sẵn

    // Cập nhật average rating
    @Modifying
    @Transactional
    @Query("UPDATE Location l SET l.averageRating = :newRating WHERE l.id = :locationId")
    int updateAverageRating(@Param("locationId") Integer locationId, @Param("newRating") BigDecimal newRating); // Trả về int
}