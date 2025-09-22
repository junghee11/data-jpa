package com.develop.datajpa.response.kakao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class KakaoPayErrorDto {

    private int error_code;
    private String error_message;
    private Map<String, Object> extras; // 원천사(머니/카드) 실패 응답

}
