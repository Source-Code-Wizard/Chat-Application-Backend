package com.poc.chat.application.controller;


import com.poc.chat.application.domain.dto.ChatMessageDto;
import com.poc.chat.application.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public void processMessage(@Payload ChatMessageDto chatMessageDto) {
        messageService.processMessage(chatMessageDto);
    }

//    @MessageMapping("/chat.join")
//    public void joinChat(@Payload JoinChatDto joinChatDto) {
//        // Handle user joining a chat
//    }
}
