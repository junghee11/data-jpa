package com.develop.domain.dto.kakao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//@Data
@Getter
@Setter
@ToString
public class KakaoPayReadyDto {

    private String tid; // 결제 고유 번호
    private String next_redirect_mobile_url; // 모바일 웹 링크
    private String next_redirect_pc_url; // pc 웹 결제 링크
    private String next_redirect_app_url; // 앱 결제 링크
    private String android_app_scheme; // 안드로이드 결제시
    private String ios_app_scheme; // ios 결제시
    private String created_at;

}
