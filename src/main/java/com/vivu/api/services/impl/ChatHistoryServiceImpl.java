package com.vivu.api.services.impl;

import com.vivu.api.entities.ChatHistory;
import com.vivu.api.entities.User;
import com.vivu.api.enums.SenderType;
import com.vivu.api.repositories.ChatHistoryRepository;
import com.vivu.api.repositories.UserRepository;
import com.vivu.api.services.ChatHistoryService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.Collections;
import java.util.List;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryServiceImpl.class);

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private UserRepository userRepository; // Để lấy đối tượng User

    @Override
    @Transactional // Cần transaction để đảm bảo lưu thành công
    public void saveUserMessage(String sessionId, Integer userId, String message) {
        saveMessage(sessionId, userId, message, SenderType.USER);
    }

    @Override
    @Transactional
    public void saveBotMessage(String sessionId, Integer userId, String message) {
        saveMessage(sessionId, userId, message, SenderType.BOT);
    }

    private void saveMessage(String sessionId, Integer userId, String message, SenderType senderType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for chat history: " + userId));

        ChatHistory history = ChatHistory.builder()
                .sessionId(sessionId)
                .user(user)
                .senderType(senderType)
                .messageContent(message)
                // timestamp tự động gán
                .build();
        chatHistoryRepository.save(history);
        logger.debug("Saved {} message for session {}: {}", senderType, sessionId, message.substring(0, Math.min(message.length(), 50)) + "...");
    }

    @Override
    @Transactional(readOnly = true) // Chỉ đọc
    public List<ChatHistory> getRecentHistory(String sessionId, Integer userId, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        // Lấy 'limit' tin nhắn mới nhất (DESC), sau đó đảo ngược lại để đúng thứ tự thời gian (ASC)
        Pageable pageable = PageRequest.of(0, limit);
        List<ChatHistory> recentMessagesDesc = chatHistoryRepository.findBySessionIdAndUserIdOrderByTimestampDesc(sessionId, userId, pageable);
        Collections.reverse(recentMessagesDesc); // Đảo ngược thành thứ tự ASC
        return recentMessagesDesc;
    }

    @Override
    @Transactional
    public void deleteHistoryForUser(Integer userId) {
        long deletedCount = chatHistoryRepository.deleteByUserId(userId);
        logger.info("Deleted {} chat history records for user {}", deletedCount, userId);
    }
}