package com.vivu.api.controllers;

import com.vivu.api.dtos.chatbot.ChatRequestDto;
import com.vivu.api.dtos.chatbot.ChatResponseDto;
import com.vivu.api.dtos.common.ApiResponse;
import com.vivu.api.security.services.UserDetailsImpl;
import com.vivu.api.services.ChatHistoryService;
import com.vivu.api.services.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import nếu cần bảo vệ
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;
    @Autowired // *** INJECT ChatHistoryService ***
    private ChatHistoryService chatHistoryService;

    @PostMapping("/message")
    @PreAuthorize("isAuthenticated()") // *** Yêu cầu người dùng đăng nhập để chat ***
    public ResponseEntity<ChatResponseDto> handleChatMessage(@Valid @RequestBody ChatRequestDto requestDto) {
        ChatResponseDto response = chatbotService.handleMessage(requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me/chatbot-history") // Dùng /me/ để chỉ user hiện tại
    @PreAuthorize("isAuthenticated()") // Yêu cầu đăng nhập
    public ResponseEntity<ApiResponse> deleteMyChatHistory(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        try {
            chatHistoryService.deleteHistoryForUser(userDetails.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Chat history deleted successfully."));
        } catch (Exception e) {
            // Log lỗi nếu cần thiết
            Logger logger = LoggerFactory.getLogger(ChatbotController.class);
            logger.error("Error deleting chat history for user {}", userDetails.getId(), e); // Cần khai báo logger trong class này
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Failed to delete chat history."));
        }
    }
}