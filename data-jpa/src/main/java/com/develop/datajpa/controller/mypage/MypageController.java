package com.develop.datajpa.controller.mypage;


import com.develop.core.security.jwt.CustomUserDetails;
import com.develop.datajpa.request.mypage.CreateChatRoomRequest;
import com.develop.datajpa.request.mypage.GetChatMessageListRequest;
import com.develop.datajpa.request.mypage.SelectMyTeamRequest;
import com.develop.datajpa.service.mypage.MypageService;
import com.develop.websocket.redis.dto.ChatRoomCacheDto;
import com.develop.websocket.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mypage")
public class MypageController {

    private final MypageService mypageService;
    private final ChatService chatService;

    @GetMapping("/baseball/team")
    public Map<String, Object> getMyTeamInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return mypageService.getMyTeamInfo(userDetails.getLoginInfo());
    }

    @PatchMapping("/baseball/team")
    public Map<String, Object> selectMyTeam(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @Valid @RequestBody SelectMyTeamRequest request) {
        return mypageService.selectMyTeam(userDetails.getLoginInfo(), request);
    }

    @GetMapping("/baseball/stadium")
    public Map<String, Object> getMyStadiumList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return mypageService.getMyStadiumList(userDetails.getLoginInfo());
    }

    @PatchMapping("/baseball/stadium/{id}")
    public Map<String, Object> toggleStadium(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable(value = "id") int id) {
        return mypageService.toggleStadium(userDetails.getLoginInfo(), id);
    }

    @GetMapping("/baseball/player")
    public Map<String, Object> getMyPlayerList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return mypageService.getMyPlayerList(userDetails.getLoginInfo());
    }

    @PatchMapping("/baseball/player/{id}")
    public Map<String, Object> togglePlayer(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable(value = "id") int id) {
        return mypageService.togglePlayer(userDetails.getLoginInfo(), id);
    }

    @GetMapping("/article")
    public Map<String, Object> getMyArticleList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return mypageService.getMyArticleList(userDetails.getLoginInfo());
    }

    @GetMapping("/comment")
    public Map<String, Object> getMyCommentList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return mypageService.getMyCommentList(userDetails.getLoginInfo());
    }

    @GetMapping("/shop/cart")
    public Map<String, Object> getMyCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return mypageService.getMyCart(userDetails.getLoginInfo());
    }

    @GetMapping("/shop/wish")
    public Map<String, Object> getMyWishList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return mypageService.getMyWishList(userDetails.getLoginInfo());
    }

    @GetMapping("/shop/purchase")
    public Map<String, Object> getMyPurchaseList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return mypageService.getMyPurchaseList(userDetails.getLoginInfo());
    }

    @GetMapping("/shop/purchase/{id}")
    public Map<String, Object> getMyPurchaseDetail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable(value = "id") String receiptCode) {
        return mypageService.getMyPurchaseDetail(userDetails.getLoginInfo(), receiptCode);
    }

    @PostMapping("/profile/image")
    public Map<String, Object> changeProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestPart(value = "file") MultipartFile file) {
        return mypageService.changeProfileImage(userDetails.getLoginInfo(), file);
    }

    @GetMapping("/chat/room")
    public Map<String, Object> getChatRoomList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return mypageService.getChatRoomList(userDetails.getLoginInfo());
    }

    @GetMapping("/chat/friend")
    public Map<String, Object> getFriendList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return mypageService.getFriendList(userDetails.getLoginInfo());
    }

    @GetMapping("/chat/message")
    public Map<String, Object> getChatMessage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @Valid GetChatMessageListRequest request) {
        return mypageService.getChatMessageList(userDetails.getLoginInfo(), request);
    }

    @PostMapping("/chat/room")
    public Map<String, Object> createChatRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @Valid @RequestBody CreateChatRoomRequest request) {
        ChatRoomCacheDto room = chatService.createChatRoom(
            userDetails.getLoginInfo().getUserId(),
            request.getRoomType(),
            request.getRoomName(),
            request.getParticipants()
        );

        return Map.of("room", room);
    }

    @DeleteMapping("/chat/room/{roomId}")
    public Map<String, Object> leaveChatRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable(value = "roomId") String roomId) {
        chatService.leaveChat(roomId, userDetails.getLoginInfo().getUserId());

        return Map.of("message", "채팅방을 나갔습니다");
    }

}
