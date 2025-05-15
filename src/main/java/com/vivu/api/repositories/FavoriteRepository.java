package com.vivu.api.repositories;

import com.vivu.api.entities.Favorite;
import com.vivu.api.entities.User;
import com.vivu.api.enums.FavoriteType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    // Tìm danh sách yêu thích của user với phân trang
    // Lưu ý: Cần JOIN để lấy thông tin Tour/Location sau ở Service hoặc DTO projection
    Page<Favorite> findByUserId(Integer userId, Pageable pageable);

    // Tìm danh sách yêu thích của user theo loại (Tour/Location) với phân trang
    Page<Favorite> findByUserIdAndFavoriteType(Integer userId, FavoriteType favoriteType, Pageable pageable);

    // Kiểm tra xem user đã yêu thích mục này chưa
    Optional<Favorite> findByUserIdAndFavoriteTypeAndFavoriteId(Integer userId, FavoriteType favoriteType, Integer favoriteId);

    // Xóa một mục yêu thích cụ thể của user
    @Transactional // Cần Transactional cho các thao tác delete/update tùy chỉnh
    long deleteByUserIdAndFavoriteTypeAndFavoriteId(Integer userId, FavoriteType favoriteType, Integer favoriteId);

    @Modifying
    @Transactional
    void deleteByUser(User user); // Xóa theo đối tượng User
}