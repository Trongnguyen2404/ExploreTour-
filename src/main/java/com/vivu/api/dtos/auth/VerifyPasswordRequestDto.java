package com.vivu.api.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyPasswordRequestDto {
    @NotBlank(message = "Current password cannot be blank")
    private String currentPassword;
}