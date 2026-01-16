package com.poc.chat.application.service.impl;

import com.poc.chat.application.domain.dto.FriendRequestDto;
import com.poc.chat.application.domain.dto.FriendRequestResponseDto;
import com.poc.chat.application.domain.dto.FriendRequestUpdateDto;
import com.poc.chat.application.domain.dto.UserRequestDto;
import com.poc.chat.application.domain.dto.UserResponseDto;
import com.poc.chat.application.domain.entity.ChatEntity;
import com.poc.chat.application.domain.entity.FriendRequestEntity;
import com.poc.chat.application.domain.entity.FriendRequestId;
import com.poc.chat.application.domain.entity.UserEntity;
import com.poc.chat.application.domain.mapper.UserMapper;
import com.poc.chat.application.repository.ChatRepository;
import com.poc.chat.application.repository.FriendRequestRepository;
import com.poc.chat.application.repository.UserRepository;
import com.poc.chat.application.service.UserService;
import com.poc.chat.application.util.enums.FriendRequestStatus;
import com.poc.chat.application.util.enums.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Override
    @Transactional
    public UserResponseDto createUser(final UserRequestDto userRequestDto) {
        log.info("Trying to save new user with username : {}", userRequestDto.username());
        final UserEntity savedEntity = userRepository.save(UserMapper.toEntity(userRequestDto));
        return UserMapper.toResponseDto(savedEntity);
    }

    @Override
    @Transactional
    public FriendRequestResponseDto saveFriendRequest(final String receiverId, final FriendRequestDto friendRequestDto) {

        final UserEntity receiver = userRepository.findById(UUID.fromString(receiverId))
                .orElseThrow(() -> new RuntimeException(String.format("Receiver with id : %s of friend request was not found.", UUID.fromString(receiverId))));

        final UserEntity sender = userRepository.findById(friendRequestDto.senderId())
                .orElseThrow(() -> new RuntimeException(String.format("Sender with id : %s of friend request was not found.", friendRequestDto.senderId())));

        friendRequestRepository.save(new FriendRequestEntity(sender, receiver));

        return new FriendRequestResponseDto(UUID.fromString(receiverId), friendRequestDto.senderId(), FriendRequestStatus.PENDING);
    }

    @Override
    @Transactional
    public FriendRequestResponseDto updateFriendRequestStatus(final String receiverId, final FriendRequestUpdateDto friendRequestUpdateDto) {

        final UserEntity receiver = userRepository.findById(UUID.fromString(receiverId))
                .orElseThrow(() -> new RuntimeException(String.format("Requester with id : %s of friend request was not found.", UUID.fromString(receiverId))));

        final UserEntity sender = userRepository.findById(friendRequestUpdateDto.senderId())
                .orElseThrow(() -> new RuntimeException(String.format("friend with id : %s of friend request was not found.", friendRequestUpdateDto.senderId())));

        final FriendRequestEntity friendRequestEntity = friendRequestRepository.findById(new FriendRequestId(friendRequestUpdateDto.senderId(), UUID.fromString(receiverId)))
                .orElseThrow(() -> new RuntimeException(String.format("Friend request from : %s to : %s was not found.", friendRequestUpdateDto.senderId(), UUID.fromString(receiverId))));

        friendRequestEntity.updateStatus(friendRequestUpdateDto.status());

        friendRequestRepository.save(friendRequestEntity);

        if (friendRequestUpdateDto.status().equals(FriendRequestStatus.ACCEPTED)) {
            ChatEntity newPrivateChat = new ChatEntity();
            newPrivateChat.addUser(receiver, UserRole.USER);
            newPrivateChat.addUser(sender, UserRole.USER);
            chatRepository.save(newPrivateChat);
        }

        return new FriendRequestResponseDto(UUID.fromString(receiverId), friendRequestUpdateDto.senderId(), friendRequestUpdateDto.status());
    }

}
