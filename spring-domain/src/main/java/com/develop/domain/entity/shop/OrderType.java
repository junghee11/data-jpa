package com.develop.domain.entity.shop;

public class OrderType {

    public enum Payment {
        //        CREDIT_CARD,
//        SAMSUNG_PAY, // 일단 kakaoPay api만 연결,, 다른 결제 api 추가 연동 시 수정 필요
        KAKAO_PAY
    }

}
