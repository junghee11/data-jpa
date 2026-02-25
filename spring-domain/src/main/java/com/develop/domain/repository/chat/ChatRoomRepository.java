package com.develop.domain.repository.chat;

import com.develop.domain.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query(value = "SELECT * FROM chat_room cr WHERE :participant = ANY(cr.participants)", nativeQuery = true)
    List<ChatRoom> findByParticipant(@Param("participant") String participant);

}
