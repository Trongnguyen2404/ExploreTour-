package com.vivu.api.repositories;

import com.vivu.api.entities.RefreshToken;
import com.vivu.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    // Tìm refresh token bằng chuỗi token
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser_Id(Integer userId);
    // Xóa tất cả refresh token của một user (dùng khi logout hoặc đổi pass)
    @Modifying // Cần thiết cho thao tác DELETE
    @Transactional // Đảm bảo transaction
    int deleteByUser(User user); // Trả về số lượng bản ghi đã xóa

    // Xóa các token đã hết hạn (dùng cho scheduled task)
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate <= :now")
    int deleteExpiredTokens(@Param("now") Instant now);
}