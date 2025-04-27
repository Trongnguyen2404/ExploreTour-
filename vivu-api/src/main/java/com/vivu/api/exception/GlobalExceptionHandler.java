package com.vivu.api.exception;

import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.exception.TokenRefreshException; // Import exception tùy chỉnh
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Kết hợp @ControllerAdvice và @ResponseBody
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Lỗi không tìm thấy thực thể (ví dụ: user, tour, location id không tồn tại)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Trả về 404
    public ApiResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.warn("Entity not found: {}", ex.getMessage());
        return new ApiResponse(false, ex.getMessage());
    }

    // Lỗi không có quyền truy cập (ví dụ: user thường gọi API admin)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // Trả về 403
    public ApiResponse handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        return new ApiResponse(false, "Access Denied: You do not have permission to perform this action.");
    }

    // Lỗi validation dữ liệu đầu vào không hợp lệ (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Trả về 400
    public ApiResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.warn("Validation failed: {}", errors);
        // Lấy lỗi đầu tiên để hiển thị
        String firstErrorMessage = errors.values().stream().findFirst().orElse("Validation Failed");
        return new ApiResponse(false, firstErrorMessage); // Chỉ trả về message lỗi đầu tiên
        // Hoặc trả về tất cả lỗi: return new ApiResponse(false, "Validation Failed", errors);
    }

    // Lỗi liên quan đến Refresh Token (ví dụ: hết hạn, không tồn tại)
    @ExceptionHandler(TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // Trả về 403
    public ApiResponse handleTokenRefreshException(TokenRefreshException ex) {
        logger.warn("Token Refresh Error: {}", ex.getMessage());
        return new ApiResponse(false, ex.getMessage());
    }

    // Lỗi IllegalArgumentException (ví dụ: tour code đã tồn tại khi tạo mới)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Trả về 400
    public ApiResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        return new ApiResponse(false, ex.getMessage());
    }


    // Xử lý các lỗi chung khác không được bắt cụ thể ở trên
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Trả về 500
    public ApiResponse handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex); // Log cả stack trace cho lỗi 500
        return new ApiResponse(false, "An unexpected error occurred. Please try again later.");
    }
}