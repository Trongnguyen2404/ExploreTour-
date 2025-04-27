package com.vivu.api.dtos.chatbot;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequestDto {
    @NotBlank(message = "Message cannot be blank")
    private String message;
    private String sessionId; // Có thể null cho tin nhắn đầu tiên
}