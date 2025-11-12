package com.develop.domain.repository.chat;

import com.develop.domain.entity.chat.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    long countBySenderIdAndCreatedAtAfter(String senderId, LocalDateTime time);

    List<ChatMessage> findByRoomId(String roomId, Pageable pageable);

}
