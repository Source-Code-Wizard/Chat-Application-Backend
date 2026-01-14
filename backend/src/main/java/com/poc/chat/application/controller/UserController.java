package com.poc.chat.application.controller;

import com.poc.chat.application.domain.dto.FriendRequestDto;
import com.poc.chat.application.domain.dto.FriendRequestResponseDto;
import com.poc.chat.application.domain.dto.UserRequestDto;
import com.poc.chat.application.domain.dto.UserResponseDto;
import com.poc.chat.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    ResponseEntity<UserResponseDto> create(@RequestBody final UserRequestDto userRequestDto) {
        return ResponseEntity
                .ok(userService.createUser(userRequestDto));
    }

    @PostMapping("/{receiverId}/friend-requests")
    ResponseEntity<FriendRequestResponseDto> saveFriendRequest(@PathVariable("receiverId") final String receiverId, @RequestBody final FriendRequestDto friendRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.saveFriendRequest(receiverId, friendRequestDto));
    }

    @PatchMapping("/{receiverId}/friend-requests")
    ResponseEntity<FriendRequestResponseDto> updateFriendRequestStatus(@PathVariable("receiverId") final String receiverId, @RequestBody final FriendRequestDto friendRequestDto) {
        return ResponseEntity
                .ok(userService.saveFriendRequest(receiverId, friendRequestDto));
    }
}
