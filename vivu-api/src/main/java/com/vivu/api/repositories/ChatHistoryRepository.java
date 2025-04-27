package com.vivu.api.repositories;

import com.vivu.api.entities.ChatHistory;
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Import Modifying
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    // Tìm lịch sử gần nhất của một session và user, giới hạn số lượng
    List<ChatHistory> findBySessionIdAndUserIdOrderByTimestampDesc(String sessionId, Integer userId, Pageable pageable);

    // Tìm tất cả lịch sử của một user (dùng để xóa)
    List<ChatHistory> findByUserId(Integer userId);

    // Xóa tất cả lịch sử của một user
    @Modifying
    @Transactional
    long deleteByUserId(Integer userId); // Trả về số dòng đã xóa
}