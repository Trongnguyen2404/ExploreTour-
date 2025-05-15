package com.vivu.api.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = "token")
}, indexes = {
        @Index(name = "idx_refresh_token", columnList = "token"),
        @Index(name = "idx_refresh_token_user", columnList = "user_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Liên kết với User

    @Column(nullable = false, unique = true)
    private String token; // Chuỗi refresh token (UUID)

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate; // Thời gian hết hạn

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}