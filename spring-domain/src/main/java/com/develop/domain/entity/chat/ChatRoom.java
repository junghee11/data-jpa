package com.develop.domain.entity.chat;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.develop.core.exception.ClientException;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import com.develop.domain.entity.chat.ChatType.RoomType;

import javax.crypto.KeyGenerator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @Column(name = "room_id")
    private String id;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "room_type", columnDefinition = "room_type")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(name = "created_by")
    private String createdBy;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "varchar[]")
    private String[] participants;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void addParticipant(String host, String guest) {
        if(!Arrays.asList(this.participants).contains(host)) {
            throw new ClientException("해당 채팅방에 대한 권한이 없습니다");
        }

        if(Arrays.asList(this.participants).contains(guest)) {
            throw new ClientException("이미 참여중인 채팅방입니다");
        }

        List<String> updated = new ArrayList<>(Arrays.asList(this.participants == null ? new String[]{} : this.participants));
        updated.add(guest);
        this.participants = updated.toArray(String[]::new);
    }

    public boolean rejoinParticipant(String userId) {
        List<String> updated = new ArrayList<>(Arrays.asList(this.participants == null ? new String[]{} : this.participants));
        if (updated.contains(userId)) {
            return false;
        }

        updated.add(userId);
        this.participants = updated.toArray(String[]::new);
        return true;
    }

    public void removeParticipant(String userId) {
        if(!Arrays.asList(this.participants).contains(userId)) {
            throw new ClientException("채팅방에 참여중이 아닙니다");
        }

        List<String> updated = new ArrayList<>(Arrays.asList(this.participants));
        updated.remove(userId);
        this.participants = updated.toArray(String[]::new);
    }

    public static String generateRoodId(RoomType type, String[] participants) {
        switch (type) {
            case DIRECT :
                return "DM_" + String.join("_", Arrays.stream(participants).sorted().toArray(String[]::new));
            case GROUP :
                return "GRP_" + System.currentTimeMillis() + "_" +
                        UUID.randomUUID().toString().replace("_", "").substring(0,6);
            case PUBLIC :
                return "PUB_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        + "_" + UUID.randomUUID().toString().replace("_", "").substring(0,4);
        }

        throw new ClientException("잘못된 요청입니다");
    }

    @Builder
    public ChatRoom(RoomType roomType, String roomName, String createdBy, String[] participants) {
        this.id = generateRoodId(roomType, participants);
        this.roomType = roomType;
        this.roomName = roomName;
        this.createdBy = createdBy;
        this.participants = participants;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
}
