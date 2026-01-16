package com.poc.chat.application.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserEntityId implements Serializable {

    @Column(name = "USER_ID")
    private UUID userId;

    @Column(name = "CHAT_ID")
    private UUID chatId;

    @Override
    public boolean equals(Object o) {

        if (o == null) return false;

        if (o == this) return true;

        if (getClass() != o.getClass()) return false;

        ChatUserEntityId that = (ChatUserEntityId) o;
        return Objects.equals(that.userId, this.userId) && Objects.equals(that.chatId, this.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, userId);
    }

}
