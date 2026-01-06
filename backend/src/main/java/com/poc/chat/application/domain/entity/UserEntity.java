package com.poc.chat.application.domain.entity;


import com.poc.chat.application.util.enums.UserStatus;
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

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER")
public class UserEntity {

    @Id
    @UuidGenerator
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "AVATAR_URL", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20)
    private UserStatus status = UserStatus.OFFLINE;

    @Column(name = "LAST_SEEN_AT")
    private LocalDateTime lastSeenAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
