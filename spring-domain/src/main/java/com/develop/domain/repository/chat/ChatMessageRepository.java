package com.develop.domain.repository.chat;

import com.develop.domain.entity.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    long countBySenderIdAndCreatedAtAfter(String senderId, LocalDateTime time);

}
