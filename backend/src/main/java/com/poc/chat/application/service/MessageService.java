package com.poc.chat.application.service;

import com.poc.chat.application.domain.dto.ChatMessageDto;

public interface MessageService {

    void processMessage(final ChatMessageDto chatMessageDto);

}
