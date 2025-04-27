package com.vivu.api.services;

import com.vivu.api.dtos.chatbot.ChatRequestDto;
import com.vivu.api.dtos.chatbot.ChatResponseDto;

public interface ChatbotService {
    ChatResponseDto handleMessage(ChatRequestDto requestDto);
}