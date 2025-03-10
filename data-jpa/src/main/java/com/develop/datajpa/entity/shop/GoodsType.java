package com.develop.datajpa.entity.shop;

public class GoodsType {

    public enum State {
        NORMAL("정상"),
        REMOVED("삭제됨"),
        FORBIDDEN("관리자에 의해 판매중지 처리됨");

        private final String value;

        State(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }
    }

}
