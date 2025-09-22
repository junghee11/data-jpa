package com.develop.domain.dto.kakao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KakaoPayGetInfoDto {

    // 유저에게 공개해도 되는 정보만 불러오기 위해 우선 민감해보이는 정보는 주석처리함..

    private String tid; // 결제 고유 번호
    //    private String cid; // 가맹점 코드
    private String status; // 결제 상태
    //    private String partner_order_id; // 가맹점 주문 번호
//    private String partner_user_id; // 가맹점 회원 id
    private String payment_method_type; // 결제 수단
    private KakaoPayAmountDto amount; // 결제 금액 정보
    private String item_name; // 상품명
    //    private String item_code; // 상품 코드
    private int quantity; // 상품 수량
    private String created_at; // 결제 요청 시간
    private String approved_at; // 결제 승인 시간
    private String canceled_at; // 결제 승인 시간
//    private List<KakaoPayCancelDetailDto> payment_action_details; // 결제/취소 상세

}
