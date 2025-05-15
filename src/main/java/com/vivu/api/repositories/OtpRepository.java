// src/main/java/com/vivu/api/repositories/OtpRepository.java
package com.vivu.api.repositories;

import com.vivu.api.entities.Otp;
import com.vivu.api.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List; // Nếu có phương thức trả về List
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer> {

    // Tìm OTP hợp lệ (chưa sử dụng, chưa hết hạn) theo email, code và mục đích
    Optional<Otp> findByEmailAndOtpCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
            String email, String otpCode, OtpPurpose purpose, Instant now);

    // Tìm OTP mới nhất theo email và mục đích (để vô hiệu hóa OTP cũ nếu cần)
    Optional<Otp> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);

    // Xóa tất cả các OTP đã hết hạn hoặc đã sử dụng (dùng cho Scheduled task)
    @Modifying
    @Transactional
    @Query("DELETE FROM Otp o WHERE o.isUsed = true OR o.expiresAt <= :now")
    void deleteExpiredAndUsedOtps(@Param("now") Instant now);

    // Vô hiệu hóa tất cả OTP cũ của một email cho mục đích cụ thể (khi tạo OTP mới)
    @Modifying
    @Transactional
    @Query("UPDATE Otp o SET o.isUsed = true WHERE o.email = :email AND o.purpose = :purpose AND o.isUsed = false")
    void invalidateOldOtps(@Param("email") String email, @Param("purpose") OtpPurpose purpose);

    // --- THÊM PHƯƠNG THỨC NÀY ---
    /**
     * Deletes all OTP records associated with the given email address.
     * Used primarily during hard user deletion.
     * @param email The email address whose OTPs should be deleted.
     */
    @Modifying
    @Transactional
    void deleteByEmail(String email);
    // -----------------------------

}