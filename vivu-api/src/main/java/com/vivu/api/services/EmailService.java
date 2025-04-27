package com.vivu.api.services;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otpCode, String purpose);
    // Có thể thêm các phương thức gửi mail khác (vd: welcome email)
}