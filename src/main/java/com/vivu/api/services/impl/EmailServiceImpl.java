package com.vivu.api.services.impl;

import com.vivu.api.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async; // Để gửi mail bất đồng bộ
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender; // Cần cấu hình bean này

    @Value("${spring.mail.username}") // Lấy địa chỉ mail gửi đi từ config
    private String fromEmail;

    @Override
    @Async // Chạy bất đồng bộ để không block request chính
    public void sendOtpEmail(String toEmail, String otpCode, String purposeSubject) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Vivu App - " + purposeSubject + " OTP"); // Tiêu đề mail
            message.setText("Your One-Time Password (OTP) \n\n " + purposeSubject + " is: " + otpCode +
                    "\n\nThis OTP is valid for a limited time. Please do not share it with anyone." +
                    "\n\n\nThank you,\nThe Vivu App Team");

            emailSender.send(message);
            logger.info("OTP email sent successfully to {}", toEmail);
        } catch (MailException exception) {
            logger.error("Error sending OTP email to {}: {}", toEmail, exception.getMessage());
            // Có thể thêm logic retry hoặc thông báo lỗi ở đây
        }
    }

    // Cấu hình Spring Mail Sender trong application.properties:
    /*
    spring.mail.host=smtp.example.com
    spring.mail.port=587
    spring.mail.username=your-email@example.com
    spring.mail.password=your-email-password
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    # Thêm các properties khác nếu cần (vd: SSL)
    */
    // Và bật @EnableAsync trong class Application chính
}