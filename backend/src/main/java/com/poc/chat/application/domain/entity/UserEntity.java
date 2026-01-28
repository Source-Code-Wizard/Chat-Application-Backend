package com.poc.chat.application.domain.entity;


import com.poc.chat.application.util.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
@NaturalIdCache // This caches lookups by the natural ID (the name field marked with @NaturalId).
// This enables second-level caching for the user entity.
// Instead of hitting the database every time you load a User, Hibernate can retrieve it from an in-memory cache.
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NaturalId
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

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ChatUserEntity> chatUserEntities;

    @OneToMany(mappedBy = "receiverEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<FriendRequestEntity> friendRequests;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageEntity> messageEntities = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addMessage(final MessageEntity messageEntity) {

        if (Objects.isNull(messageEntity))
            return;

        messageEntity.setSender(this);
        messageEntities.add(messageEntity);
    }

    public void removeMessage(final MessageEntity messageEntity) {

        if (Objects.isNull(messageEntity))
            return;

        messageEntity.setChatEntity(null);
        messageEntities.remove(messageEntity);
    }
}
