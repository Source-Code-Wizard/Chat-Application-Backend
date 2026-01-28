package com.poc.chat.application.service.impl;

import com.poc.chat.application.domain.dto.ChatMessageDto;
import com.poc.chat.application.domain.entity.ChatEntity;
import com.poc.chat.application.domain.entity.MessageEntity;
import com.poc.chat.application.domain.entity.UserEntity;
import com.poc.chat.application.repository.ChatRepository;
import com.poc.chat.application.repository.MessageRepository;
import com.poc.chat.application.repository.UserRepository;
import com.poc.chat.application.service.MessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    @Transactional
    public void processMessage(final ChatMessageDto chatMessageDto) {

        if (Objects.isNull(chatMessageDto)) return;

        final MessageEntity messageEntity = MessageEntity.builder()
                .content(chatMessageDto.content())
                .build();

        final UserEntity sender = userRepository.findById(chatMessageDto.senderId())
                .orElseThrow(() -> new RuntimeException("Sender was not fount"));

        final ChatEntity chat = chatRepository.findById(chatMessageDto.chatId())
                .orElseThrow(() -> new RuntimeException("Chat was not fount"));

        sender.addMessage(messageEntity);
        chat.addMessage(messageEntity);

        // The owning side is the entity that contains the foreign key column.
        // JPA only tracks changes on the owning side for relationship updates.
        messageRepository.save(messageEntity);

        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatMessageDto.chatId(), chatMessageDto);
    }
}
