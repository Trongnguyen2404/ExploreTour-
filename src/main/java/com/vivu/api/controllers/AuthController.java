package com.vivu.api.controllers;

import com.vivu.api.dtos.auth.*;
import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.exception.TokenRefreshException;
import com.vivu.api.services.AuthService;
import com.vivu.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    // --- Endpoints Đăng ký Mới ---
    @PostMapping("/register/request-otp")
    public ResponseEntity<ApiResponse> requestRegistrationOtp(@Valid @RequestBody RegisterRequestOtpDto requestDto) {
        ApiResponse response = authService.requestRegistrationOtp(requestDto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // Email đã tồn tại
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<ApiResponse> verifyRegistrationOtp(@Valid @RequestBody RegisterVerifyOtpDto requestDto) {
        ApiResponse response = authService.verifyRegistrationOtp(requestDto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // OTP sai/hết hạn
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register/complete")
    public ResponseEntity<ApiResponse> completeRegistration(@Valid @RequestBody RegisterCompleteRequestDto requestDto) {
        ApiResponse response = authService.completeRegistration(requestDto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // Lỗi (pass ko khớp, otp sai, user/email tồn tại)
            return ResponseEntity.badRequest().body(response);
        }
    }

    // --- Quên/Đặt lại Mật khẩu ---
    @PostMapping("/forgot-password") // Bước 1: Yêu cầu OTP
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        ApiResponse response = authService.requestPasswordReset(forgotPasswordRequest);
        return ResponseEntity.ok(response); // Luôn OK
    }
    // THÊM: Bước 2 - Xác thực OTP quên mật khẩu
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse> verifyForgotPasswordOtp(@Valid @RequestBody VerifyOtpRequestDto requestDto) {
        ApiResponse response = authService.verifyForgotPasswordOtp(requestDto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response); // OTP sai/hết hạn
        }
    }

    // THÊM: Bước 3 - Đặt mật khẩu mới sau khi OTP đã xác thực
    @PostMapping("/forgot-password/set-new-password")
    public ResponseEntity<ApiResponse> setNewPasswordAfterOtpVerification(@Valid @RequestBody SetNewPasswordRequestDto requestDto) {
        ApiResponse response = authService.setNewPasswordAfterOtpVerification(requestDto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // Lỗi (pass ko khớp, otp sai/hết hạn lại)
            return ResponseEntity.badRequest().body(response);
        }
    }

    // --- Đổi Mật khẩu (Đã đăng nhập) ---
    @PostMapping("/change-password")
    // @PreAuthorize("isAuthenticated()") // Đã được bảo vệ
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        ApiResponse response = authService.changePassword(changePasswordRequest, authentication);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // --- Đổi Email (Đã đăng nhập - Luồng 3 bước) ---

    // Bước 1: Xác thực mật khẩu hiện tại
    @PostMapping("/verify-password-for-email-change") // *** ENDPOINT ĐÃ THÊM LẠI ***
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> verifyPasswordForEmailChange(
            @Valid @RequestBody VerifyPasswordRequestDto requestDto, // Dùng DTO chỉ chứa mật khẩu
            Authentication authentication) {
        ApiResponse response = authService.verifyPasswordForEmailChange(requestDto, authentication);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response); // Chỉ xác nhận mật khẩu đúng
        } else {
            // Sai mật khẩu
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Trả 401 (Unauthorized)
        }
    }

    // Bước 2: Yêu cầu OTP cho email mới (vẫn cần gửi mật khẩu hiện tại trong DTO này)
    @PostMapping("/request-email-change")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> requestEmailChange(
            @Valid @RequestBody UpdateEmailRequest updateEmailRequest, // DTO này chứa cả pass và email mới
            Authentication authentication) {
        ApiResponse response = authService.requestEmailChange(updateEmailRequest, authentication);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response); // Gửi OTP thành công
        } else {
            // Lỗi (sai pass, email trùng, ...)
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Bước 3: Xác thực OTP và hoàn tất đổi email
    @PostMapping("/verify-email-change")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> verifyEmailChange(
            @Valid @RequestBody VerifyNewEmailRequest verifyNewEmailRequest, // DTO chứa email mới và OTP
            Authentication authentication) {
        ApiResponse response = authService.verifyOtpAndChangeEmail(verifyNewEmailRequest, authentication);
        if (response.isSuccess()) {
            // Response thành công chứa token mới trong data
            return ResponseEntity.ok(response);
        } else {
            // Lỗi (OTP sai/hết hạn, email bị trùng)
            return ResponseEntity.badRequest().body(response);
        }
    }

    // --- Logout & Refresh Token ---
    @PostMapping("/logout") 
    // @PreAuthorize("isAuthenticated()") // Vẫn nên giữ nếu dùng @EnableMethodSecurity
    public ResponseEntity<ApiResponse> logoutUser(Authentication authentication,
                                                  @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) { // Nhận header Authorization, không bắt buộc

        String token = null;
        // Trích xuất token từ header "Bearer <token>"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else {
            // Ghi log nếu không có header hoặc không đúng định dạng
            logger.warn("Authorization header is missing or not in Bearer format during logout.");
        }

        // Gọi service với Authentication và token (có thể là null)
        ApiResponse response = userService.logoutUser(authentication, token);
        return ResponseEntity.ok(response);
    }
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        try {
            TokenRefreshResponseDto response = authService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (TokenRefreshException ex) {
            // Trả về 403 Forbidden khi refresh token không hợp lệ/hết hạn
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, ex.getMessage()));
        }
    }
}