package com.vivu.api.services;

import com.vivu.api.entities.ChatHistory;

import java.util.List;

public interface ChatHistoryService {
    // Lưu tin nhắn từ User
    void saveUserMessage(String sessionId, Integer userId, String message);

    // Lưu tin nhắn từ Bot
    void saveBotMessage(String sessionId, Integer userId, String message);

    // Lấy lịch sử gần nhất cho một session
    List<ChatHistory> getRecentHistory(String sessionId, Integer userId, int limit);

    // Xóa toàn bộ lịch sử của một user
    void deleteHistoryForUser(Integer userId);
}