package com.poc.chat.application.domain.entity;

import com.poc.chat.application.util.enums.ChatType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CHAT")
public class ChatEntity {

    @Id
    @UuidGenerator
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "NAME", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 20)
    @Builder.Default
    private ChatType type = ChatType.PRIVATE;

    @Column(name = "ICON_URL", length = 500)
    private String iconUrl;

    @Column(name = "LAST_ACTIVITY_AT")
    private LocalDateTime lastActivityAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastActivityAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
