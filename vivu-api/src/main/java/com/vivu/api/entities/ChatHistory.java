package com.vivu.api.entities;

import com.vivu.api.enums.SenderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp; // Dùng CreationTimestamp tốt hơn

import java.time.Instant;

@Entity
@Table(name = "chat_histories", indexes = {
        @Index(name = "idx_chat_session_user", columnList = "session_id, user_id"),
        @Index(name = "idx_chat_user_time", columnList = "user_id, timestamp")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Dùng Long cho phù hợp với BIGINT

    @Column(name = "session_id", length = 36, nullable = false)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;

    @Lob // Đảm bảo lưu được text dài
    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @CreationTimestamp // Tự động gán thời điểm tạo
    @Column(nullable = false, updatable = false)
    private Instant timestamp;
}