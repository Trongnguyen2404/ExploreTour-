package com.vivu.api.services.impl;

import com.vivu.api.dtos.auth.*;
import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.entities.Otp;
import com.vivu.api.entities.RefreshToken;
import com.vivu.api.entities.User;
import com.vivu.api.enums.OtpPurpose;
import com.vivu.api.enums.Role;
import com.vivu.api.exception.TokenRefreshException;
import com.vivu.api.repositories.OtpRepository; // Import OtpRepository
import com.vivu.api.repositories.UserRepository;
import com.vivu.api.security.jwt.JwtUtils;
import com.vivu.api.security.services.UserDetailsImpl;
import com.vivu.api.services.AuthService;
import com.vivu.api.services.EmailService;
import com.vivu.api.services.OtpService;
import com.vivu.api.services.RefreshTokenService; // Import RefreshTokenService
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional; // Import Optional

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OtpRepository otpRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    OtpService otpService;

    @Autowired
    EmailService emailService;

    @Autowired
    RefreshTokenService refreshTokenService; // Inject RefreshTokenService

    @Override
    public JwtResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Tạo refresh token mới (xóa cái cũ nếu có trong service)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new JwtResponse(jwt, refreshToken.getToken(), userDetails); // Trả về cả hai token
    }

    // --- Luồng Đăng Ký Mới (3 bước) ---
    @Override
    @Transactional // Cần Transactional vì có thao tác DB (kiểm tra tồn tại) và gửi mail
    public ApiResponse requestRegistrationOtp(RegisterRequestOtpDto requestDto) {
        String email = requestDto.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmailIgnoringStatus(email)) {
            logger.warn("Registration OTP requested for already registered email: {}", email);
            return new ApiResponse(false, "This email address is already registered.");
        }

        Otp otp = otpService.generateAndSaveOtp(email, OtpPurpose.REGISTER_VERIFICATION);
        emailService.sendOtpEmail(email, otp.getOtpCode(), "Account Registration Verification");
        logger.info("Registration verification OTP sent to: {}", email);
        return new ApiResponse(true, "Verification OTP sent to your email.");
    }

    @Override
    @Transactional(readOnly = true) // Chỉ kiểm tra
    public ApiResponse verifyRegistrationOtp(RegisterVerifyOtpDto requestDto) {
        boolean isValid = otpService.validateOtp(
                requestDto.getEmail().toLowerCase().trim(),
                requestDto.getOtp(),
                OtpPurpose.REGISTER_VERIFICATION
        );

        if (isValid) {
            logger.info("Registration OTP verified successfully for: {}", requestDto.getEmail());
            return new ApiResponse(true, "OTP verified successfully. You can now complete your registration.");
        } else {
            logger.warn("Invalid or expired registration OTP for: {}", requestDto.getEmail());
            return new ApiResponse(false, "Invalid or expired OTP.");
        }
    }

    @Override
    @Transactional
    public ApiResponse completeRegistration(RegisterCompleteRequestDto requestDto) {
        String email = requestDto.getEmail().toLowerCase().trim();
        String username = requestDto.getUsername().trim();

        if (!requestDto.getPassword().equals(requestDto.getRepeatPassword())) {
            return new ApiResponse(false, "Passwords do not match!");
        }

        boolean isOtpStillValid = otpService.validateOtp(
                email,
                requestDto.getOtp(),
                OtpPurpose.REGISTER_VERIFICATION
        );
        if (!isOtpStillValid) {
            logger.warn("Attempt to complete registration with invalid/expired OTP for email: {}", email);
            return new ApiResponse(false, "Invalid or expired OTP. Please request a new one.");
        }

        if (userRepository.existsByUsernameIgnoringStatus(username)) {
            logger.warn("Attempt to register with existing username: {}", username);
            return new ApiResponse(false, "Username is already taken!");
        }
        if (userRepository.existsByEmailIgnoringStatus(email)) {
            logger.error("Concurrency issue: Email {} got registered during OTP verification!", email);
            return new ApiResponse(false, "This email address was just registered. Please try logging in.");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.USER)
                .isActive(true)
                .build();
        userRepository.save(user);

        Optional<Otp> otpOptional = otpRepository.findByEmailAndOtpCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
                email, requestDto.getOtp(), OtpPurpose.REGISTER_VERIFICATION, java.time.Instant.now().minusSeconds(10)
        );
        if(otpOptional.isPresent()){
            otpService.markOtpAsUsed(otpOptional.get());
            logger.info("Registration OTP marked as used for {}", email);
        } else {
            logger.error("Could not find the verified OTP to mark as used for email: {}. Manual cleanup might be needed.", email);
        }

        logger.info("User registered and completed successfully: {} ({})", username, email);
        return new ApiResponse(true, "Registration completed successfully!");
    }

    // --- Quên Mật Khẩu Cập Nhật (3 bước) ---
    @Override
    @Transactional
    public ApiResponse requestPasswordReset(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail()).orElse(null);

        if (user == null) {
            logger.warn("Password reset requested for non-existent or inactive email: {}", forgotPasswordRequest.getEmail());
            return new ApiResponse(true, "If an account with that email exists, a password reset OTP has been sent.");
        }

        Otp otp = otpService.generateAndSaveOtp(user.getEmail(), OtpPurpose.FORGOT_PASSWORD);
        emailService.sendOtpEmail(user.getEmail(), otp.getOtpCode(), "Password Reset");
        logger.info("Password reset OTP sent to: {}", user.getEmail());
        return new ApiResponse(true, "Password reset OTP sent to your email. Please verify the OTP.");
    }

    @Override
    @Transactional(readOnly = true) // Chỉ xác thực
    public ApiResponse verifyForgotPasswordOtp(VerifyOtpRequestDto requestDto) {
        boolean isValid = otpService.validateOtp(
                requestDto.getEmail().toLowerCase().trim(),
                requestDto.getOtp(),
                OtpPurpose.FORGOT_PASSWORD
        );

        if (isValid) {
            logger.info("Forgot password OTP verified successfully for: {}", requestDto.getEmail());
            return new ApiResponse(true, "OTP verified successfully. You can now set your new password.");
        } else {
            logger.warn("Invalid or expired forgot password OTP for: {}", requestDto.getEmail());
            return new ApiResponse(false, "Invalid or expired OTP.");
        }
    }

    @Override
    @Transactional
    public ApiResponse setNewPasswordAfterOtpVerification(SetNewPasswordRequestDto requestDto) {
        if (!requestDto.getNewPassword().equals(requestDto.getRepeatPassword())) {
            return new ApiResponse(false, "New passwords do not match!");
        }

        String email = requestDto.getEmail().toLowerCase().trim();
        String otpCode = requestDto.getOtp();

        boolean isOtpStillValid = otpService.validateOtp(email, otpCode, OtpPurpose.FORGOT_PASSWORD);
        if (!isOtpStillValid) {
            logger.warn("Attempt to set new password with invalid/expired forgot password OTP for email: {}", email);
            return new ApiResponse(false, "Invalid or expired OTP. Please request a new one.");
        }

        User user = userRepository.findByEmailIgnoringStatus(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        user.setPasswordHash(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);

        Optional<Otp> otpOptional = otpRepository.findByEmailAndOtpCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
                email, otpCode, OtpPurpose.FORGOT_PASSWORD, java.time.Instant.now().minusSeconds(10)
        );
        if(otpOptional.isPresent()){
            otpService.markOtpAsUsed(otpOptional.get());
            logger.info("Forgot password OTP marked as used for {}", email);
        } else {
            logger.error("Could not find the verified forgot password OTP to mark as used for email: {}.", email);
        }

        // Xóa refresh token của user này khi đổi pass thành công
        refreshTokenService.deleteByUserId(user.getId());

        logger.info("Password reset successfully for: {}", user.getEmail());
        return new ApiResponse(true, "Password has been reset successfully. Please log in with your new password.");
    }

    // --- Đổi Mật khẩu (Đã đăng nhập) ---
    @Override
    @Transactional
    public ApiResponse changePassword(ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), currentUser.getPasswordHash())) {
            return new ApiResponse(false, "Incorrect current password.");
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getRepeatNewPassword())) {
            return new ApiResponse(false, "New passwords do not match.");
        }

        currentUser.setPasswordHash(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(currentUser);

        // Xóa refresh token khi đổi pass
        refreshTokenService.deleteByUserId(currentUser.getId());

        logger.info("Password changed successfully for user: {}", currentUser.getEmail());
        return new ApiResponse(true, "Password changed successfully. Please log in again.");
    }

    // --- Đổi Email (Đã đăng nhập - 3 bước) ---
    @Override
    @Transactional(readOnly = true)
    public ApiResponse verifyPasswordForEmailChange(VerifyPasswordRequestDto requestDto, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), currentUser.getPasswordHash())) {
            logger.warn("Incorrect password provided during email change verification step for user {}", userDetails.getEmail());
            return new ApiResponse(false, "Incorrect password.");
        }

        logger.info("Password verified successfully for email change process for user {}", userDetails.getEmail());
        return new ApiResponse(true, "Password verified successfully. Please enter your new email address.");
    }

    @Override
    @Transactional
    public ApiResponse requestEmailChange(UpdateEmailRequest updateEmailRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(updateEmailRequest.getCurrentPassword(), currentUser.getPasswordHash())) {
            logger.warn("Incorrect password provided during email change request step for user {}", userDetails.getEmail());
            return new ApiResponse(false, "Incorrect password provided with new email request.");
        }

        String newEmail = updateEmailRequest.getNewEmail().toLowerCase().trim();
        if (currentUser.getEmail().equalsIgnoreCase(newEmail)) {
            return new ApiResponse(false, "New email cannot be the same as the current email.");
        }
        if (userRepository.existsByEmailIgnoringStatus(newEmail)) {
            return new ApiResponse(false, "Error: New email is already in use!");
        }

        Otp otp = otpService.generateAndSaveOtp(newEmail, OtpPurpose.VERIFY_NEW_EMAIL);
        emailService.sendOtpEmail(newEmail, otp.getOtpCode(), "Email Change Verification");

        logger.info("Email change verification OTP sent to new email: {}", newEmail);
        return new ApiResponse(true, "Verification OTP sent to your new email address. Please enter the OTP to complete the change.");
    }

    @Override
    @Transactional
    public ApiResponse verifyOtpAndChangeEmail(VerifyNewEmailRequest verifyNewEmailRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String newEmail = verifyNewEmailRequest.getNewEmail().toLowerCase().trim();

        boolean isValidOtp = otpService.validateOtp(newEmail, verifyNewEmailRequest.getOtp(), OtpPurpose.VERIFY_NEW_EMAIL);
        if (!isValidOtp) {
            return new ApiResponse(false, "Invalid or expired OTP for the new email.");
        }

        if (!currentUser.getEmail().equalsIgnoreCase(newEmail) && userRepository.existsByEmailIgnoringStatus(newEmail)) {
            logger.warn("New email {} became occupied during OTP verification for user {}", newEmail, currentUser.getEmail());
            return new ApiResponse(false, "Error: New email is already in use!");
        }

        currentUser.setEmail(newEmail);
        userRepository.save(currentUser);

        Optional<Otp> otpOptional = otpRepository.findByEmailAndOtpCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
                newEmail, verifyNewEmailRequest.getOtp(), OtpPurpose.VERIFY_NEW_EMAIL, java.time.Instant.now().minusSeconds(10)
        );
        if(otpOptional.isPresent()){
            otpService.markOtpAsUsed(otpOptional.get());
            logger.info("Email change OTP marked as used for {}", newEmail);
        } else {
            logger.error("Could not find the verified email change OTP to mark as used for email: {}.", newEmail);
        }

        logger.info("Email successfully changed for user {} to {}", userDetails.getId(), newEmail);

        // Tạo lại Authentication và Access Token mới
        UserDetailsImpl updatedUserDetails = UserDetailsImpl.build(currentUser);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                updatedUserDetails, null, updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        String newToken = jwtUtils.generateJwtToken(newAuthentication);

        // Giữ lại Refresh Token cũ
        RefreshToken existingRefreshToken = refreshTokenService.findByTokenUserId(currentUser.getId()).orElse(null);

        JwtResponse jwtResponse = new JwtResponse(newToken, existingRefreshToken != null ? existingRefreshToken.getToken() : null, updatedUserDetails);

        return new ApiResponse(true, "Email changed successfully.", jwtResponse);
    }

    // --- Refresh Token ---
    @Override
    public TokenRefreshResponseDto refreshToken(RefreshTokenRequestDto request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getEmail());
                    logger.info("Generated new access token for user {} using refresh token", user.getEmail());
                    return new TokenRefreshResponseDto(token, requestRefreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }
}