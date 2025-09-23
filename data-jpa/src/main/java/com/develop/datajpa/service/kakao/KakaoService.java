package com.develop.datajpa.service.kakao;

import com.develop.core.exception.ClientException;
import com.develop.datajpa.response.kakao.*;
import com.develop.domain.entity.shop.Receipt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class KakaoService {

    @Value("${spring.kakao.pay.cid}")
    String cid; // 가맹점 테스트 코드

    @Value("${spring.kakao.pay.cid.secret}")
    String cid_secret; // 가맹점 테스트 코드
    private KakaoPayReadyDto kakaoReady;

    private static final String KAKAO_PAY_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private static final String KAKAO_PAY_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";
    private static final String KAKAO_PAY_GET_INFO_URL = "https://open-api.kakaopay.com/online/v1/payment/order";
    private static final String KAKAO_PAY_CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";

    public KakaoPayReadyDto kakaoPayReady(String itemCode, String itemName, long totalPrice) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("partner_order_id", "가맹점 주문 번호");
        requestBody.put("partner_user_id", "가맹점 회원 ID");
        requestBody.put("item_name", itemName);
        requestBody.put("item_code", itemCode);
        requestBody.put("quantity", 1); // 주문 수량
        requestBody.put("total_amount", totalPrice); // 총 금액
//        requestBody.put("vat_amount", totalPrice / 10); // 부가세
        requestBody.put("tax_free_amount", 1); // 상품 비과세 금액??
        requestBody.put("approval_url", "http://localhost:8080/shop/kakao-pay/success"); // 성공 시 redirect url
        requestBody.put("cancel_url", "http://localhost:8080/shop/kakao-pay/cancel"); // 취소 시 redirect url
        requestBody.put("fail_url", "http://localhost:8080/shop/kakao-pay/fail"); // 실패 시 redirect url

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, this.getHeaders());

        try {
            RestTemplate restTemplate = new RestTemplate();
            kakaoReady = restTemplate.postForObject(
                    KAKAO_PAY_READY_URL,
                    requestEntity,
                    KakaoPayReadyDto.class
            );

            return kakaoReady;
        } catch (HttpClientErrorException e) {
            throw new ClientException("Fail to call KakaoPay Ready API : " + e.getMessage());
        }
    }

    public KakaoPayApproveDto approveResponse(String pgToken) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("tid", kakaoReady.getTid());
        requestBody.put("partner_order_id", "가맹점 주문 번호");
        requestBody.put("partner_user_id", "가맹점 회원 ID");
        requestBody.put("pg_token", pgToken);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, this.getHeaders());

        try {
            RestTemplate restTemplate = new RestTemplate();
            KakaoPayApproveDto approveResponse = restTemplate.postForObject(
                    KAKAO_PAY_APPROVE_URL,
                    requestEntity,
                    KakaoPayApproveDto.class
            );

            return approveResponse;

        } catch (HttpClientErrorException e) {
            KakaoPayErrorDto error = e.getResponseBodyAs(KakaoPayErrorDto.class);

            throw new ClientException("Fail to call KakaoPay Approve API : " + error.getError_message());
        }

    }

    public KakaoPayGetInfoDto getPayInfo(String tid) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("tid", tid);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, this.getHeaders());

        try {
            RestTemplate restTemplate = new RestTemplate();
            KakaoPayGetInfoDto response = restTemplate.postForObject(
                    KAKAO_PAY_GET_INFO_URL,
                    requestEntity,
                    KakaoPayGetInfoDto.class
            );

            return response;

        } catch (HttpClientErrorException e) {
            throw new ClientException("결제정보가 확인되지 않습니다.\n오류코드 : " + e.getMessage());
        }

    }

    public KakaoPayCancelDto kakaoPayCancel(Receipt receipt, String reason) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("tid", receipt.getReceiptCode());
        requestBody.put("cancel_amount", receipt.getTotalPrice()); // 취소 금액
        requestBody.put("cancel_tax_free_amount", 1); // 취소 비과세 금액 ??
//        requestBody.put("cancel_vat_amount", 10); // 취소 부가세 금액 (승인시 vat_amount를 보냈다면 취소시에도 동일하게 요청..)
        requestBody.put("payload", reason); // 해당 요청에 대해 저장하고 싶은 값, 최대 200자

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, this.getHeaders());

        try {
            RestTemplate restTemplate = new RestTemplate();
            KakaoPayCancelDto response = restTemplate.postForObject(
                    KAKAO_PAY_CANCEL_URL,
                    requestEntity,
                    KakaoPayCancelDto.class
            );

            return response;

        } catch (HttpClientErrorException e) {
            KakaoPayErrorDto error = e.getResponseBodyAs(KakaoPayErrorDto.class);

            System.out.println("error code : " + error.getError_code());

            throw new ClientException("Fail to call KakaoPay Cancel API : " + error.getError_message());
        }

    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + cid_secret);

        return headers;
    }

}
