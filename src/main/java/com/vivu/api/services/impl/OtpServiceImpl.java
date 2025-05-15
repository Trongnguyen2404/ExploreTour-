package com.vivu.api.services.impl;

import com.vivu.api.entities.Otp;
import com.vivu.api.enums.OtpPurpose;
import com.vivu.api.repositories.OtpRepository;
import com.vivu.api.services.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class OtpServiceImpl implements OtpService {
    private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);


    @Autowired
    private OtpRepository otpRepository;

    // Cấu hình thời gian hết hạn OTP (vd: 5 phút)
    @Value("${vivu.app.otpExpirationMinutes:5}") // Lấy từ properties, mặc định 5 phút
    private int otpExpirationMinutes;

    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public Otp generateAndSaveOtp(String email, OtpPurpose purpose) {
        // Vô hiệu hóa các OTP cũ cùng loại cho email này
        otpRepository.invalidateOldOtps(email, purpose);

        String otpCode = generateOtpCode(6); // Tạo mã OTP 6 chữ số
        Instant expiresAt = Instant.now().plus(otpExpirationMinutes, ChronoUnit.MINUTES);

        Otp otp = Otp.builder()
                .email(email)
                .otpCode(otpCode)
                .purpose(purpose)
                .expiresAt(expiresAt)
                .isUsed(false)
                .build();

        Otp savedOtp = otpRepository.save(otp);
        logger.info("Generated OTP {} for {} with purpose {}", otpCode, email, purpose);
        return savedOtp;
    }

    @Override
    @Transactional(readOnly = true) // Chỉ đọc, không cần transaction ghi
    public boolean validateOtp(String email, String otpCode, OtpPurpose purpose) {
        Optional<Otp> otpOptional = otpRepository.findByEmailAndOtpCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
                email, otpCode, purpose, Instant.now()
        );

        if (otpOptional.isPresent()) {
            logger.info("OTP validation successful for {} with purpose {}", email, purpose);
            // Không đánh dấu đã sử dụng ở đây, chỉ validate
            return true;
        } else {
            logger.warn("OTP validation failed for {} with purpose {}. Invalid code, expired, or already used.", email, purpose);
            return false;
        }
    }

    @Override
    @Transactional
    public void markOtpAsUsed(Otp otp) {
        // Tìm lại Otp từ DB để đảm bảo tính nhất quán
        Optional<Otp> freshOtp = otpRepository.findById(otp.getId());
        if (freshOtp.isPresent() && !freshOtp.get().isUsed()) {
            Otp otpToUpdate = freshOtp.get();
            otpToUpdate.setUsed(true);
            otpRepository.save(otpToUpdate);
            logger.info("Marked OTP {} as used for {}", otp.getId(), otp.getEmail());
        } else {
            logger.warn("Attempted to mark non-existent or already used OTP {} as used.", otp.getId());
        }
    }

    @Override
    @Transactional
    // Thường được gọi từ một Scheduled Task
    public void cleanupExpiredOtps() {
        Instant now = Instant.now();
        logger.info("Running OTP cleanup task at {}", now);
        otpRepository.deleteExpiredAndUsedOtps(now);
        // Log số lượng đã xóa nếu cần (method repository nên trả về int)
    }


    // Helper tạo mã OTP ngẫu nhiên
    private String generateOtpCode(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Số từ 0-9
        }
        return otp.toString();
    }
}