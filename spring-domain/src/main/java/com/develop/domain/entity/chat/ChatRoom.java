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
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private RoomType roomType;

    @Column(name = "created_by")
    private String createdBy;

    @ElementCollection
    @Column(columnDefinition = "text[]")
    private List<String> participants;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

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
    public ChatRoom(String id, String roomName, RoomType roomType, String createdBy, List<String> participants) {
        this.id = id;   // TODO: DM 채팅방의 경우 생성전에 id 부분 검증 필요!
        this.roomName = roomName;
        this.roomType = roomType;
        this.createdBy = createdBy;
        this.participants = participants;
    }
}
