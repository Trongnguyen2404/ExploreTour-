package com.vivu.api.services;

import com.vivu.api.entities.Otp;
import com.vivu.api.enums.OtpPurpose;

public interface OtpService {
    Otp generateAndSaveOtp(String email, OtpPurpose purpose);
    boolean validateOtp(String email, String otpCode, OtpPurpose purpose);
    void markOtpAsUsed(Otp otp);
    void cleanupExpiredOtps(); // Dùng cho scheduled task
}