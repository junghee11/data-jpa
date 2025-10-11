package com.develop.domain.entity.chat;

public class ChatType {

    public enum MessageType {
        ENTER("입장"),
        LEAVE("퇴장"),
        TALK("채팅");

        private final String value;

        MessageType(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }
    }

    public enum RoomType {
        DIRECT("DM"),
        GROUP("그룹채팅"),
        PUBLIC("오픈채팅");

        private final String value;

        RoomType(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }
    }

}
