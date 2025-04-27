package com.vivu.api.entities; // Đã cập nhật

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
// import java.util.List; // Chỉ import nếu có relationship cần

@Entity
@Table(name = "tours", uniqueConstraints = {
        @UniqueConstraint(columnNames = "tour_code")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(name = "main_image_url", length = 512, nullable = false)
    private String mainImageUrl;

    @Column(name = "location_name", length = 100, nullable = false)
    private String locationName;

    @Column(name = "itinerary_duration", length = 50, nullable = false)
    private String itineraryDuration;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "available_slots", nullable = false)
    private Integer availableSlots = 0;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.valueOf(0.00);

    @Column(name = "tour_code", length = 50, unique = true)
    private String tourCode;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "schedule_image_url", length = 512)
    private String scheduleImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_admin_id")
    private User createdByAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_admin_id")
    private User updatedByAdmin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Relationships (sẽ xử lý ở service)
}