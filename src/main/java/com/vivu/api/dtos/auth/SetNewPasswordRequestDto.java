package com.vivu.api.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetNewPasswordRequestDto {
    @NotBlank @Email
    private String email;

    @NotBlank(message = "Verified OTP cannot be blank") // OTP đã xác thực ở bước trước
    @Size(min = 6, max = 6)
    private String otp; // Gửi lại OTP đã xác thực

    @NotBlank
    @Size(min = 6, max = 100)
    private String newPassword;

    @NotBlank
    private String repeatPassword;
}