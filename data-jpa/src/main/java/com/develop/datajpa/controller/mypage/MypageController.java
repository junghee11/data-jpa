package com.develop.datajpa.controller.mypage;


import com.develop.datajpa.request.mypage.SelectMyTeamRequest;
import com.develop.datajpa.service.mypage.MypageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.develop.datajpa.service.security.JwtProvider.resolveToken;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mypage")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/baseball/team")
    public Map<String, Object> getMyTeamInfo(@RequestHeader(value = "Authorization") String token) {
        return mypageService.getMyTeamInfo(resolveToken(token));
    }

    @PatchMapping("/baseball/team")
    public Map<String, Object> selectMyTeam(@RequestHeader(value = "Authorization") String token,
                                            @Valid @RequestBody SelectMyTeamRequest request) {
        return mypageService.selectMyTeam(resolveToken(token), request);
    }

    @GetMapping("/baseball/stadium")
    public Map<String, Object> getMyStadiumList(@RequestHeader(value = "Authorization") String token) {
        return mypageService.getMyStadiumList(resolveToken(token));
    }

    @PatchMapping("/baseball/stadium/{id}")
    public Map<String, Object> toggleStadium(@RequestHeader(value = "Authorization") String token,
                                             @PathVariable(value = "id") int id) {
        return mypageService.toggleStadium(resolveToken(token), id);
    }

    @GetMapping("/baseball/player")
    public Map<String, Object> getMyPlayerList(@RequestHeader(value = "Authorization") String token) {
        return mypageService.getMyPlayerList(resolveToken(token));
    }

    @PatchMapping("/baseball/player/{id}")
    public Map<String, Object> togglePlayer(@RequestHeader(value = "Authorization") String token,
                                            @PathVariable(value = "id") int id) {
        return mypageService.togglePlayer(resolveToken(token), id);
    }

    @GetMapping("/article")
    public Map<String, Object> getMyArticleList(@RequestHeader(value = "Authorization") String token) {
        return mypageService.getMyArticleList(resolveToken(token));
    }

    @GetMapping("/comment")
    public Map<String, Object> getMyCommentList(@RequestHeader(value = "Authorization") String token) {
        return mypageService.getMyCommentList(resolveToken(token));
    }

    @GetMapping("/shop/cart")
    public Map<String, Object> getMyCart(@RequestHeader(value = "Authorization") String token) {
        return mypageService.getMyCart(resolveToken(token));
    }

    @GetMapping("/shop/wish")
    public Map<String, Object> getMyWishList(@RequestHeader(value = "Authorization") String token) {
        return mypageService.getMyWishList(resolveToken(token));
    }

    @GetMapping("/shop/purchase")
    public Map<String, Object> getMyPurchaseList(@RequestHeader(value = "Authorization") String token) {
        return mypageService.getMyPurchaseList(resolveToken(token));
    }

    @GetMapping("/shop/purchase/{id}")
    public Map<String, Object> getMyPurchaseDetail(@RequestHeader(value = "Authorization") String token,
                                                   @PathVariable(value = "id") String receiptCode) {
        return mypageService.getMyPurchaseDetail(resolveToken(token), receiptCode);
    }

    @PostMapping("/profile/image")
    public Map<String, Object> changeProfileImage(@RequestHeader(value = "Authorization") String token,
                                                  @RequestPart(value = "file") MultipartFile file) {
        return mypageService.changeProfileImage(resolveToken(token), file);
    }

}
