package com.develop.datajpa.controller.baseball;


import com.develop.core.exception.ClientException;
import com.develop.core.security.jwt.CustomUserDetails;
import com.develop.datajpa.request.baseball.GetGoodsListRequest;
import com.develop.datajpa.request.shop.AddCartRequest;
import com.develop.datajpa.request.shop.LeaveGoodsReviewRequest;
import com.develop.datajpa.request.shop.ModifyGoodsReviewRequest;
import com.develop.datajpa.request.shop.PurchaseGoodsRequest;
import com.develop.datajpa.service.baseball.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public Map<String, Object> getGoodsInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable("id") String id) {
        return shopService.getGoodsInfo(userDetails, id);
    }

    @PostMapping("/goods/wish/{id}")
    public Map<String, Object> toggleWish(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @PathVariable("id") String id) {
        return shopService.toggleWish(userDetails.getLoginInfo(), id);
    }

    @PostMapping("/goods/cart")
    public Map<String, Object> addCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @Valid @RequestBody AddCartRequest request) {
        return shopService.addCart(userDetails.getLoginInfo(), request);
    }

    @DeleteMapping("/goods/cart/{id}")
    public Map<String, Object> removeCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @PathVariable("id") String id) {
        return shopService.removeCart(userDetails.getLoginInfo(), id);
    }

    @DeleteMapping("/goods/cart")
    public Map<String, Object> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return shopService.clearCart(userDetails.getLoginInfo());
    }

    @PostMapping("/goods")
    public Map<String, Object> purchaseGoods(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @Valid @RequestBody PurchaseGoodsRequest request) {
        return shopService.purchaseGoods(userDetails.getLoginInfo(), request);
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
    public Map<String, Object> kakaoPayCancel(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable("code") String receiptCode) {
        return shopService.cancelPayment(userDetails.getLoginInfo(), receiptCode);
    }

    @GetMapping("/kakao-pay/info/{code}")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> getPayInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @PathVariable("code") String receiptCode) {
        return shopService.getPaymentInfo(userDetails.getLoginInfo(), receiptCode);
    }

    @PostMapping("/goods/order-cart")
    public Map<String, Object> orderShoppingCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return shopService.orderShoppingCart(userDetails.getLoginInfo());
    }

    @PostMapping("/goods/review")
    public Map<String, Object> leaveReview(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @Valid @RequestBody LeaveGoodsReviewRequest request) {
        return shopService.leaveReview(userDetails.getLoginInfo(), request);
    }

    @PatchMapping("/goods/review")
    public Map<String, Object> modifyGoodsReview(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @Valid @RequestBody ModifyGoodsReviewRequest request) {
        return shopService.modifyGoodsReview(userDetails.getLoginInfo(), request);
    }

    @DeleteMapping("/goods/review/{id}")
    public Map<String, Object> deleteReview(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable("id") long id) {
        return shopService.deleteReview(userDetails.getLoginInfo(), id);
    }

}
