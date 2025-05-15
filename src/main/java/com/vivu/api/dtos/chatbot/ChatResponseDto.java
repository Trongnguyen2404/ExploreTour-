package com.vivu.api.dtos.chatbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDto {
    private String reply;
    private List<AppSuggestionDto> suggestions;
    private String sessionId; // Trả về sessionId (đặc biệt quan trọng cho tin nhắn đầu)
}