package com.vivu.api.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateEmailRequest {
    @NotBlank(message = "Current password cannot be blank")
    private String currentPassword;

    @NotBlank(message = "New email cannot be blank")
    @Email(message = "New email should be valid")
    private String newEmail;
}