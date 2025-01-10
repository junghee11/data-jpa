package com.develop.datajpa.dto.kakao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KakaoPayCancelDetailDto {

    private String aid; // 요청 고유 번호
    private String approved_at; // 결제 승인 시간
    private int amount; // 결제/취소 총액
    private int point_amount; // 결제/취소 포인트 금액
    private int discount_amount; // 할인 금액
    private int green_deposit; // 컵 보증금
    private String payment_action_type; // 결제 타입 -> PAYMENT(결제), CANCEL(결제취소), ISSUED_SID(SID 발급)
    private String payload; // 취소 요청 시 전달한 값

}
