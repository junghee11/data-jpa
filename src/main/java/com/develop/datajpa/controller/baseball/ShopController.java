package com.develop.datajpa.controller.baseball;


import com.develop.datajpa.entity.MatchType.TeamCode;
import com.develop.datajpa.request.baseball.AddCartRequest;
import com.develop.datajpa.service.baseball.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.develop.datajpa.service.security.JwtProvider.resolveToken;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/goods/{team}")
    public Map<String, Object> getGoodsList(@PathVariable("team") TeamCode team) {
        return shopService.getGoodsList(team);
    }

    @GetMapping("/goods/detail/{idx}")
    public Map<String, Object> getGoodsInfo(@RequestHeader(value = "Authorization", required = false) String token,
                                            @PathVariable("idx") long idx) {
        return shopService.getGoodsInfo(token, idx);
    }

    @PostMapping("/goods/wish/{id}")
    public Map<String, Object> toggleWish(@RequestHeader(value = "Authorization") String token,
                                          @PathVariable("id") long id) {
        return shopService.toggleWish(resolveToken(token), id);
    }

    @PostMapping("/goods/cart")
    public Map<String, Object> addCart(@RequestHeader(value = "Authorization") String token,
                                       @Valid @RequestBody AddCartRequest request) {
        return shopService.addCart(resolveToken(token), request);
    }

    @DeleteMapping("/goods/cart/{id}")
    public Map<String, Object> removeCart(@RequestHeader(value = "Authorization") String token,
                                          @PathVariable("id") long id) {
        return shopService.removeCart(resolveToken(token), id);
    }

    @DeleteMapping("/goods/cart")
    public Map<String, Object> clearCart(@RequestHeader(value = "Authorization") String token) {
        return shopService.clearCart(resolveToken(token));
    }

//    @PostMapping("/goods/{id}")
//    public Map<String, Object> purchaseItem(@RequestHeader(value = "Authorization") String token,
//                                            @RequestBody LeaveReviewRequest request) {
//        return shopService.leaveReview(resolveToken(token), request);
//    }

}
