package com.develop.datajpa.controller.user;

import com.develop.core.security.jwt.CustomUserDetails;
import com.develop.datajpa.request.user.*;
import com.develop.datajpa.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Map<String, Object> userLogin(@Valid @RequestBody UserLoginRequest request) {
        return userService.userLogin(request);
    }

    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.getUserInfo(userDetails.getLoginInfo());
    }

    @GetMapping("/check/id")
    public Map<String, Object> checkUserId(@Valid CheckUserIdRequest request) {
        return userService.checkUserId(request.getUserId());
    }

    @GetMapping("/check/nickname")
    public Map<String, Object> checkNickname(@Valid CheckNicknameRequest request) {
        return userService.checkNickname(request.getNickname());
    }

    @PostMapping("/phone-sms/verification-code")
    public Map<String, Object> sendPhoneSms(@Valid @RequestBody SendPhoneSmsRequest request) {
        return userService.sendPhoneSms(request);
    }

    @PostMapping("/check/phone")
    public Map<String, Object> checkUserPhoneCheck(@Valid @RequestBody CheckUserPhoneRequest request) {
        return userService.userPhoneCheck(request);
    }

    @PostMapping("/signup")
    public Map<String, Object> userSignUp(@Valid @RequestBody UserSignUpRequest request) {
        return userService.userSignUp(request);
    }

    @GetMapping("/user-id")
    public Map<String, Object> findUserId(@Valid FindUserIdRequest request) {
        return userService.findUserId(request);
    }

    @GetMapping("/user-pw")
    public Map<String, Object> findPassword(@Valid FindUserPwRequest request) {
        return userService.findPassword(request);
    }

    @PostMapping("/user-pw")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> resetUserPassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @Valid @RequestBody ResetUserPwRequest request) {
        return userService.resetUserPassword(userDetails.getLoginInfo(), request);
    }

    @PostMapping("/leave")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> userLeave(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.userLeave(userDetails.getLoginInfo());
    }

}
