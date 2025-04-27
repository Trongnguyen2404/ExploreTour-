package com.vivu.api.dtos.user;

import com.vivu.api.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    @NotNull(message = "Role cannot be null")
    private Role role; // USER or ADMIN
}
