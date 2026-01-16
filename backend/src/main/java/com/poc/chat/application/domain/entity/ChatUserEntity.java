package com.poc.chat.application.domain.entity;


import com.poc.chat.application.util.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CHAT_USER")
public class ChatUserEntity {

    @EmbeddedId
    private ChatUserEntityId id;

    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;

    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatEntity chatEntity;

    @Column(name = "ROLE")
    private UserRole userRole;

    @Column(name = "JOINED_AT", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "MUTED_UNTIL")
    private LocalDateTime mutedUntil;

    public ChatUserEntity(final ChatEntity chatEntity, final UserEntity userEntity, final UserRole userRole) {
        this.id = new ChatUserEntityId(userEntity.getId(), chatEntity.getId());
        this.chatEntity = chatEntity;
        this.userEntity = userEntity;
        this.userRole = userRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ChatUserEntity that = (ChatUserEntity) o;
        return Objects.equals(userEntity, that.userEntity) && Objects.equals(chatEntity, that.chatEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEntity, chatEntity);
    }

    @PrePersist
    private void onCreate() {
        joinedAt = LocalDateTime.now();
    }

}
