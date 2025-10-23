package com.develop.domain.repository.chat;

import com.develop.domain.entity.chat.ChatRoom;
import com.develop.domain.entity.chat.ChatType.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query(value = "SELECT * FROM chat_room cr WHERE :participant = ANY(cr.participants) AND cr.room_type != :roomType", nativeQuery = true)
    List<ChatRoom> findByParticipantAndRoomTypeNot(@Param("participant") String participant, @Param("roomType") String roomType);

}
