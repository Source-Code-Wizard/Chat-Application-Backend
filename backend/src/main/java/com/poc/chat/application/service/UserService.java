package com.poc.chat.application.service;

import com.poc.chat.application.domain.dto.FriendRequestDto;
import com.poc.chat.application.domain.dto.FriendRequestResponseDto;
import com.poc.chat.application.domain.dto.FriendRequestUpdateDto;
import com.poc.chat.application.domain.dto.UserRequestDto;
import com.poc.chat.application.domain.dto.UserResponseDto;

public interface UserService {
    UserResponseDto createUser(final UserRequestDto userRequestDto);
    FriendRequestResponseDto saveFriendRequest(final String receiverId, final FriendRequestDto friendRequestDto);
    FriendRequestResponseDto updateFriendRequestStatus(final String receiverId, final FriendRequestUpdateDto friendRequestUpdateDto);
}
