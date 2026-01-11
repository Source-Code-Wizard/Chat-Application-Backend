package com.poc.chat.application.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;


@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FriendRequestId implements Serializable {
    @Column(name = "SENDER_ID")
    private UUID senderId;

    @Column(name = "RECEIVER_ID")
    private UUID receiverId;
}
