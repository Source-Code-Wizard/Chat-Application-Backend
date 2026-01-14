package com.poc.chat.application.domain.mapper;


import com.poc.chat.application.domain.dto.UserRequestDto;
import com.poc.chat.application.domain.dto.UserResponseDto;
import com.poc.chat.application.domain.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserEntity toEntity(final UserRequestDto dto) {
        return UserEntity.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .avatarUrl(dto.avatarUrl())
                .build();
    }

    public static UserResponseDto toResponseDto(final UserEntity entity) {
        return new UserResponseDto(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getAvatarUrl(),
                entity.getStatus().toString(),
                entity.getLastSeenAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
