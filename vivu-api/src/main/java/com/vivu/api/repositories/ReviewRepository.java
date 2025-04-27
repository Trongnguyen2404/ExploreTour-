package com.vivu.api.repositories;

import com.vivu.api.entities.Review;
import com.vivu.api.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // Tìm review theo target (Tour hoặc Location) với phân trang
    Page<Review> findByTargetTypeAndTargetId(TargetType targetType, Integer targetId, Pageable pageable);

    // Tìm review theo người dùng với phân trang
    Page<Review> findByUserId(Integer userId, Pageable pageable);

    // Tìm một review cụ thể của user cho một target (kiểm tra xem user đã review chưa)
    Optional<Review> findByUserIdAndTargetTypeAndTargetId(Integer userId, TargetType targetType, Integer targetId);

    // Tính rating trung bình cho một target
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetType = :targetType AND r.targetId = :targetId")
    Optional<Double> getAverageRatingForTarget(@Param("targetType") TargetType targetType, @Param("targetId") Integer targetId);

    // Đếm số lượng review cho một target
    long countByTargetTypeAndTargetId(TargetType targetType, Integer targetId);
}