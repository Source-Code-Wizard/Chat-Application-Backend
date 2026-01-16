package com.poc.chat.application.domain.entity;


import com.poc.chat.application.util.enums.FriendRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FRIEND_REQUEST")
public class FriendRequestEntity {

    @EmbeddedId
    private FriendRequestId friendRequestId;

    @MapsId("senderId")
    // By default , spring boot will name this column like this : sender_entity_id
    // based on the private member variable : senderEntity
    // this is the reason we use  @JoinColumn(name = "sender_id")
    @JoinColumn(name = "sender_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity senderEntity;

    @MapsId("receiverId")
    @JoinColumn(name = "receiver_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity receiverEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private FriendRequestStatus status;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "RESPONDED_AT")
    private LocalDateTime respondedAt;

    public FriendRequestEntity(final UserEntity sender, final UserEntity receiver) {
        this.friendRequestId = new FriendRequestId(sender.getId(), receiver.getId());
        this.senderEntity = sender;
        this.receiverEntity = receiver;
    }

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = FriendRequestStatus.PENDING;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateStatus(final FriendRequestStatus newStatus) {
        status = newStatus;
        respondedAt = LocalDateTime.now();
    }

}
