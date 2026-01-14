package com.poc.chat.application.domain.dto;

import com.poc.chat.application.util.enums.FriendRequestStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FriendRequestUpdateDto(@NotNull(message = "Sender ID is required") UUID senderId,
                                     FriendRequestStatus status) {
}
