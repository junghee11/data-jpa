package com.develop.datajpa.controller.user;

import com.develop.datajpa.request.user.CheckNicknameRequest;
import com.develop.datajpa.request.user.CheckUserIdRequest;
import com.develop.datajpa.request.user.CheckUserPhoneRequest;
import com.develop.datajpa.request.user.FindUserIdRequest;
import com.develop.datajpa.request.user.FindUserPwRequest;
import com.develop.datajpa.request.user.ResetUserPwRequest;
import com.develop.datajpa.request.user.UserLoginRequest;
import com.develop.datajpa.request.user.UserSignUpRequest;
import com.develop.datajpa.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.develop.datajpa.service.security.JwtProvider.resolveToken;

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
    public Map<String, Object> getUserInfo(@RequestHeader(value = "Authorization") String token) {
        return userService.getUserInfo(resolveToken(token));
    }

    @GetMapping("/check/id")
    public Map<String, Object> checkId(@Valid @RequestBody CheckUserIdRequest request) {
        return userService.checkMemberId(request.getMemberId());
    }

    @GetMapping("/check/nickname")
    public Map<String, Object> checkNickname(@Valid @RequestBody CheckNicknameRequest request) {
        return userService.checkNickname(request.getNickname());
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
    public Map<String, Object> findUserId(@Valid @RequestBody FindUserIdRequest request) {
        return userService.findUserId(request);
    }

    @GetMapping("/user-pw")
    public Map<String, Object> findPassword(@Valid @RequestBody FindUserPwRequest request) {
        return userService.findPassword(request);
    }

    @PostMapping("/user-pw")
    public Map<String, Object> resetUserPassword(@Valid @RequestBody ResetUserPwRequest request) {
        return userService.resetUserPassword(request);
    }

}
