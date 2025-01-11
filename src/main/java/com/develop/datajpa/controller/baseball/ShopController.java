package com.develop.datajpa.controller.baseball;


import com.develop.datajpa.dto.kakao.KakaoPayReadyDto;
import com.develop.datajpa.request.baseball.GetGoodsListRequest;
import com.develop.datajpa.request.shop.AddCartRequest;
import com.develop.datajpa.request.shop.LeaveGoodsReviewRequest;
import com.develop.datajpa.request.shop.ModifyGoodsReviewRequest;
import com.develop.datajpa.request.shop.PurchaseGoodsRequest;
import com.develop.datajpa.response.ClientException;
import com.develop.datajpa.service.baseball.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.develop.datajpa.service.security.JwtProvider.resolveToken;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/goods")
    public Map<String, Object> getGoodsList(@Valid GetGoodsListRequest request) {
        return shopService.getGoodsList(request);
    }

    @GetMapping("/goods/{id}")
    public Map<String, Object> getGoodsInfo(@RequestHeader(value = "Authorization", required = false) String token,
                                            @PathVariable("id") String id) {
        return shopService.getGoodsInfo(token, id);
    }

    @PostMapping("/goods/wish/{id}")
    public Map<String, Object> toggleWish(@RequestHeader(value = "Authorization") String token,
                                          @PathVariable("id") String id) {
        return shopService.toggleWish(resolveToken(token), id);
    }

    @PostMapping("/goods/cart")
    public Map<String, Object> addCart(@RequestHeader(value = "Authorization") String token,
                                       @Valid @RequestBody AddCartRequest request) {
        return shopService.addCart(resolveToken(token), request);
    }

    @DeleteMapping("/goods/cart/{id}")
    public Map<String, Object> removeCart(@RequestHeader(value = "Authorization") String token,
                                          @PathVariable("id") String id) {
        return shopService.removeCart(resolveToken(token), id);
    }

    @DeleteMapping("/goods/cart")
    public Map<String, Object> clearCart(@RequestHeader(value = "Authorization") String token) {
        return shopService.clearCart(resolveToken(token));
    }

    // TODO : test code 추가하기
    @PostMapping("/goods")
    public KakaoPayReadyDto purchaseGoods(@RequestHeader(value = "Authorization") String token,
                                          @RequestBody PurchaseGoodsRequest request) {
        return shopService.purchaseGoods(resolveToken(token), request);
    }

    @GetMapping("/kakao-pay/success")
    public Map<String, Object> afterPayRequest(@RequestParam("pg_token") String pgToken) {
        return shopService.approvePayment(pgToken);
    }

    @GetMapping("/kakao-pay/cancel")
    public void cancel() {
        throw new ClientException("오류가 발생했습니다.\n사유 : 결제 진행 중 취소");
    }

    @GetMapping("/kakao-pay/fail")
    public void fail() {
        throw new ClientException("오류가 발생했습니다.\n사유 : 결제 실패");
    }

    @PostMapping("/kakao-pay/cancel/{code}")
    public Map<String, Object> kakaoPayCancel(@RequestHeader(value = "Authorization") String token,
                                              @PathVariable("code") String receiptCode) {
        return shopService.cancelPayment(resolveToken(token), receiptCode);
    }

    @GetMapping("/kakao-pay/info/{code}")
    public Map<String, Object> getPayInfo(@RequestHeader(value = "Authorization") String token,
                                          @PathVariable("code") String receiptCode) {
        return shopService.getPaymentInfo(resolveToken(token), receiptCode);
    }

    @PostMapping("/goods/{id}")
    public Map<String, Object> orderShoppingCart(@RequestHeader(value = "Authorization") String token) {
        return shopService.orderShoppingCart(resolveToken(token));
    }

    @PostMapping("/goods/review")
    public Map<String, Object> leaveReview(@RequestHeader(value = "Authorization") String token,
                                           @RequestBody LeaveGoodsReviewRequest request) {
        return shopService.leaveReview(resolveToken(token), request);
    }

    @PatchMapping("/goods/review")
    public Map<String, Object> modifyGoodsReview(@RequestHeader(value = "Authorization") String token,
                                                 @RequestBody ModifyGoodsReviewRequest request) {
        return shopService.modifyGoodsReview(resolveToken(token), request);
    }

    @DeleteMapping("/goods/review/{id}")
    public Map<String, Object> deleteReview(@RequestHeader(value = "Authorization") String token,
                                            @PathVariable("id") long id) {
        return shopService.deleteReview(resolveToken(token), id);
    }

}
