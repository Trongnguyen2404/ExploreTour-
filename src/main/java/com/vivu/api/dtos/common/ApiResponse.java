package com.vivu.api.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data; // Thêm data nếu cần trả về dữ liệu cụ thể

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}