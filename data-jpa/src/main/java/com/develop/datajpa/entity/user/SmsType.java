package com.develop.datajpa.entity.user;

public class SmsType {

    public enum VerificationType {
        SIGN_UP("회원가입"),
        FIND_ID("아이디 찾기"),
        FIND_PW("비밀번호 찾기");

        private final String value;

        VerificationType(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }

    }

}
