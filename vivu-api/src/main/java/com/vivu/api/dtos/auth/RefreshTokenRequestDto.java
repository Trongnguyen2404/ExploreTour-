package com.vivu.api.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {
    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;
}