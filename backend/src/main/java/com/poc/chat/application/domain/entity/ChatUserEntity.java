package com.poc.chat.application.domain.entity;


import com.poc.chat.application.util.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "CHAT_USER")
public class ChatUserEntity {

    @EmbeddedId
    private ChatUserEntityId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;

    @MapsId("chatId")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatEntity chatEntity;

    @Column(name = "ROLE")
    private UserRole userRole;

    @Column(name = "JOINED_AT", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "MUTED_UNTIL", nullable = false, updatable = false)
    private LocalDateTime mutedUntil;

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

}
