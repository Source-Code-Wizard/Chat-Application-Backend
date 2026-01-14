package com.poc.chat.application.domain.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FriendRequestDto(@NotNull(message = "Sender ID is required") UUID senderId) {
}
