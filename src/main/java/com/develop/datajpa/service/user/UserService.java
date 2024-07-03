package com.develop.datajpa.service.user;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.dto.user.UserDto;
import com.develop.datajpa.entity.User;
import com.develop.datajpa.repository.UserRepository;
import com.develop.datajpa.request.user.CheckUserPhoneRequest;
import com.develop.datajpa.request.user.FindUserIdRequest;
import com.develop.datajpa.request.user.FindUserPwRequest;
import com.develop.datajpa.request.user.ResetUserPwRequest;
import com.develop.datajpa.request.user.UserLoginRequest;
import com.develop.datajpa.request.user.UserSignUpRequest;
import com.develop.datajpa.response.ClientException;
import com.develop.datajpa.service.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.lang.module.FindException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public User checkUser(String id) {
        Optional<User> user = userRepository.findOptionalByUserId(id);
        if (user.isEmpty()) {
            throw new ClientException("가입정보가 확인되지 않습니다.");
        }

        return user.get();
    }

    public Map<String, Object> userLogin(UserLoginRequest request) {
        User user = checkUser(request.getUserId());

        if (!request.getPassword().equals(user.getPw())) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 잘못되었습니다.");
        }

        String token = jwtProvider.createToken(user);
        log.info("token = {}", token);

        return Map.of(
            "token", token
        );
    }

    public Map<String, Object> getUserInfo(LoginInfo loginInfo) {
        UserDto user = userRepository.findByUserId(loginInfo.getUserId());
        if (isNull(user)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "회원정보가 확인되지 않습니다.");
        }

        return Map.of(
            "result", user
        );
    }

    Set<String> FORBIDDEN_NAME = Set.of(
        "ADMIN", "UNDEFINED", "NULL", "관리자", "운영자", "LOCALHOST", "DEVELOP"
    );

    public Map<String, Object> checkMemberId(String memberId) {
        if (FORBIDDEN_NAME.contains(memberId.toUpperCase())) {
            throw new ClientException("사용하실 수 없는 아이디입니다.");
        }

        UserDto user = userRepository.findByUserId(memberId);
        if (nonNull(user)) {
            throw new ClientException("이미 사용중인 아이디입니다.");
        }

        return Map.of(
            "message", "사용할 수 있는 아이디입니다."
        );
    }

    public Map<String, Object> checkNickname(String nickname) {
        if (FORBIDDEN_NAME.contains(nickname.toUpperCase())) {
            throw new ClientException("사용하실 수 없는 닉네임입니다.");
        }

        User user = userRepository.findByNickname(nickname);
        if (nonNull(user)) {
            throw new ClientException("이미 사용중인 닉네임입니다.");
        }

        return Map.of(
            "message", "사용할 수 있는 닉네임입니다."
        );
    }

    public Map<String, Object> userPhoneCheck(CheckUserPhoneRequest request) {
        User user = userRepository.findByPhone(request.getPhone());
        if (nonNull(user)) {
            throw new FindException("이미 가입된 정보가 있습니다.");
        }

        // TODO : 외부 API로 작업필요
        // TODO : signup api에서 검증내용 한번 더 확인해주기
        request.getName();

        return Map.of(
            "message", "실명인증이 완료되었습니다."
        );
    }

    public Map<String, Object> userSignUp(UserSignUpRequest request) {
        User user = userRepository.findByUserIdOrNicknameOrPhone(request.getUserId(), request.getNickname(), request.getPhone());
        if (nonNull(user)) {
            throw new ClientException("이미 존재하는 회원정보입니다.");
        }

        User newMember = User.builder()
            .userId(request.getUserId())
            .name(request.getName())
            .nickname(request.getNickname())
            .phone(request.getPhone())
            .pw(request.getPassword())
            .build();
        userRepository.save(newMember);

        return Map.of(
            "message", "회원가입이 완료되었습니다."
        );
    }

    public Map<String, Object> findUserId(FindUserIdRequest request) {
        User user = userRepository.findByNameAndPhone(request.getName(), request.getPhone());
        if (isNull(user)) {
            throw new ClientException("가입하신 정보가 확인되지 않습니다.");
        }

        return Map.of(
            "userId", user.getUserId()
        );
    }

    public Map<String, Object> findPassword(FindUserPwRequest request) {
        User user = userRepository.findByUserIdAndNameAndPhone(request.getUserId(), request.getName(), request.getPhone());
        if (isNull(user)) {
            throw new ClientException("가입하신 정보가 확인되지 않습니다.");
        }

        int tempPwLength = 10;
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder tempPw = new StringBuilder(tempPwLength);

        for (int i = 0; i < tempPwLength; i++) {
            int index = random.nextInt(CHARACTERS.length());
            tempPw.append(CHARACTERS.charAt(index));
        }

        user.setPw(tempPw.toString());
        userRepository.save(user);

        return Map.of(
            "tempPw", tempPw
        );
    }

    public Map<String, Object> resetUserPassword(ResetUserPwRequest request) {
        User user = userRepository.findByUserIdAndPw(request.getUserId(), request.getTempPw());
        if (isNull(user)) {
            throw new ClientException("잘못된 요청입니다.");
        }

        user.setPw(request.getNewPw());
        userRepository.save(user);

        return Map.of(
            "message", "비밀번호가 재설정되었습니다. 새 비밀번호로 로그인 해주세요"
        );
    }
}
