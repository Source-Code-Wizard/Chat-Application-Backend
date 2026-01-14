package com.poc.chat.application.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String avatarUrl,
        String userStatus,
        LocalDateTime lastSeenAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }