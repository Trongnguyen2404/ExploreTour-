package com.vivu.api.services;

import com.vivu.api.dtos.auth.*;
import com.vivu.api.dtos.common.ApiResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    JwtResponse loginUser(LoginRequest loginRequest);
    ApiResponse requestRegistrationOtp(RegisterRequestOtpDto requestDto);
    ApiResponse verifyRegistrationOtp(RegisterVerifyOtpDto requestDto);
    ApiResponse completeRegistration(RegisterCompleteRequestDto requestDto);
    ApiResponse requestPasswordReset(ForgotPasswordRequest forgotPasswordRequest);
    ApiResponse verifyForgotPasswordOtp(VerifyOtpRequestDto requestDto); // THÊM DÒNG NÀY
    ApiResponse setNewPasswordAfterOtpVerification(SetNewPasswordRequestDto requestDto); // THÊM DÒNG NÀY
    ApiResponse changePassword(ChangePasswordRequest changePasswordRequest, Authentication authentication);
    ApiResponse requestEmailChange(UpdateEmailRequest updateEmailRequest, Authentication authentication);
    ApiResponse verifyOtpAndChangeEmail(VerifyNewEmailRequest verifyNewEmailRequest, Authentication authentication);
    // Thêm phương thức refresh token
    TokenRefreshResponseDto refreshToken(RefreshTokenRequestDto request);
    ApiResponse verifyPasswordForEmailChange(VerifyPasswordRequestDto requestDto, Authentication authentication);
}