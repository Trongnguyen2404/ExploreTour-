package com.vivu.api.dtos.user;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {

    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;

    @PastOrPresent(message = "Date of birth must be in the past or present")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // Định dạng YYYY-MM-DD
    private LocalDate dateOfBirth;

    // Pattern ví dụ cho SĐT Việt Nam, điều chỉnh nếu cần
    @Pattern(regexp = "(^$|[0-9]{10,11})", message = "Phone number must be 10 or 11 digits")
    private String phoneNumber;

    @Size(max = 512, message = "Profile picture URL is too long")
    private String profilePictureUrl; // Hoặc dùng MultipartFile nếu upload trực tiếp
}