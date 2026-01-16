package com.poc.chat.application.domain.entity;

import com.poc.chat.application.util.enums.ChatType;
import com.poc.chat.application.util.enums.UserRole;
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
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
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

    @OneToMany(mappedBy = "chatEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatUserEntity> chatUserEntities = new ArrayList<>();

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

    public void addUser(final UserEntity userEntity, final UserRole userRole) {
        final ChatUserEntity newChatUserEntity = new ChatUserEntity(this, userEntity, userRole);
        chatUserEntities.add(newChatUserEntity);
        userEntity.getChatUserEntities().add(newChatUserEntity);
    }
}
