package com.vivu.api.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterVerifyOtpDto {
    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @NotBlank(message = "OTP cannot be blank")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits") // Giả sử OTP 6 số
    private String otp;
}