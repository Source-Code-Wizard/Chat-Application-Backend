package com.poc.chat.application.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChatMessageDto(
        @NotNull(message = "Chat ID is required")
        UUID chatId,

        @NotNull(message = "Sender ID is required")
        UUID senderId,

        @NotBlank(message = "Content is required")
        @Size(max = 5000, message = "Message content must not exceed 5000 characters")
        String content) {
}
