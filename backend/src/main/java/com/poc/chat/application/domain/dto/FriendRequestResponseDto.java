package com.poc.chat.application.domain.dto;

import com.poc.chat.application.util.enums.FriendRequestStatus;

import java.util.UUID;

public record FriendRequestResponseDto(UUID receiverId, UUID senderId, FriendRequestStatus friendRequestStatus) {
}
