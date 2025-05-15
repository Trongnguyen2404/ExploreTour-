package com.vivu.api.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterCompleteRequestDto {
    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @NotBlank(message = "Verified OTP cannot be blank") // OTP đã xác thực ở bước trước
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp; // Cần gửi lại OTP đã xác thực

    @NotBlank
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message = "Repeat password cannot be blank")
    private String repeatPassword;
}