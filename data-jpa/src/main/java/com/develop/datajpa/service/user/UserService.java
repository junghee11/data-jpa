package com.develop.datajpa.service.user;

import com.develop.core.exception.ClientException;
import com.develop.core.security.dto.LoginInfo;
import com.develop.core.security.jwt.JwtTokenProvider;
import com.develop.datajpa.request.user.*;
import com.develop.datajpa.service.sms.SmsService;
import com.develop.domain.dto.user.UserDto;
import com.develop.domain.entity.user.SmsType.VerificationType;
import com.develop.domain.entity.user.SmsVerification;
import com.develop.domain.entity.user.User;
import com.develop.domain.entity.user.UserType.Role;
import com.develop.domain.repository.user.SmsVerificationRepository;
import com.develop.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.develop.datajpa.util.RandomCodeUtil.generateVerificationCode;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SmsVerificationRepository smsVerificationRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final SmsService smsService;

    public User checkUser(String id) {
        Optional<User> user = userRepository.findOptionalByUserId(id);
        if (user.isEmpty()) {
            throw new ClientException("가입정보가 확인되지 않습니다.");
        } else if (Role.DORMANT.name().equals(user.get().getRole())) {
            throw new ClientException("휴면회원 입니다. 휴면해제 후 로그인 해주세요.");
        } else if (Role.WITHDRAWAL.name().equals(user.get().getRole())) {
            throw new ClientException("탈퇴처리된 회원입니다.");
        }

        return user.get();
    }

    public User checkAdmin(String id) {
        Optional<User> user = userRepository.findOptionalByUserId(id);
        if (user.isEmpty() || !Role.ADMIN.name().equals(user.get().getRole())) {
            throw new ClientException("관리자가 아닙니다.");
        }

        return user.get();
    }

    public Map<String, Object> userLogin(UserLoginRequest request) {
        User user = checkUser(request.getUserId());

        if (!request.getPassword().equals(user.getPw())) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 잘못되었습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getUserId(), user.getNickname(),
            user.getName(), user.getRole());

        return Map.of(
                "token", token
        );
    }

    public Map<String, Object> getUserInfo(LoginInfo loginInfo) {
        UserDto user = userRepository.findByUserId(loginInfo.getUserId());
        if (isNull(user)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "회원정보가 확인되지 않습니다.");
        } else if (Role.DORMANT.name().equals(user.getRole())) {
            throw new ClientException("휴면회원 입니다. 휴면해제 후 로그인 해주세요.");
        } else if (Role.WITHDRAWAL.name().equals(user.getRole())) {
            throw new ClientException("탈퇴처리된 회원입니다.");
        }

        return Map.of(
                "result", user
        );
    }

    Set<String> FORBIDDEN_NAME = Set.of(
            "ADMIN", "UNDEFINED", "NULL", "관리자", "운영자", "LOCALHOST", "DEVELOP"
    );

    public Map<String, Object> checkUserId(String memberId) {
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

    @Transactional
    public Map<String, Object> sendPhoneSms(SendPhoneSmsRequest request) {
        User user = userRepository.findByPhone(request.getPhone());

        String code = generateVerificationCode();
        String message = "[Baseball Hub]인증번호는 [" + code + "] 입니다.";

        SmsVerification sms = smsVerificationRepository.findByPhone(request.getPhone());

        if (VerificationType.SIGN_UP.equals(request.getType())) {
            if (nonNull(user)) {
                throw new ClientException("이미 가입된 정보가 있습니다.");
            } else if (isNull(sms)) {
                SmsVerification newVerification = SmsVerification.builder()
                        .phone(request.getPhone())
                        .name(request.getName())
                        .verificationType(VerificationType.SIGN_UP)
                        .code(code)
                        .build();
                smsVerificationRepository.save(newVerification);
            } else if (sms.getState() || !VerificationType.SIGN_UP.equals(sms.getVerificationType())) {
                throw new ClientException("이미 가입된 정보가 있습니다");
            } else {
                sms.setCode(code);
                sms.setState(false);
                sms.setVerificationTime(LocalDateTime.now());
                smsVerificationRepository.save(sms);
            }

        } else if (VerificationType.FIND_ID.equals(request.getType()) || VerificationType.FIND_PW.equals(request.getType())) {
            if (isNull(user) || isNull(sms)) {
                throw new ClientException("가입정보가 확인되지 않습니다.");
            } else if (!user.getName().equals(sms.getName())) {
                throw new ClientException("기존 가입 정보와 일치하지 않습니다.");
            }

            sms.setVerificationType(request.getType());
            sms.setCode(code);
            sms.setState(false);
            sms.setVerificationTime(LocalDateTime.now());
            smsVerificationRepository.save(sms);

        } else {
            throw new ClientException("잘못된 요청입니다.");
        }

        smsService.sendSms(request.getPhone(), message);

        return Map.of(
                "message", "문자로 발송된 인증번호를 입력해주세요."
        );
    }

    @Transactional
    public Map<String, Object> userPhoneCheck(CheckUserPhoneRequest request) {
        User user = userRepository.findByPhone(request.getPhone());

        SmsVerification sms = smsVerificationRepository.findByPhoneAndName(request.getPhone(), request.getName())
                .orElseThrow(() -> new ClientException("인증 문자를 요청해주세요"));

        if (sms.getState()) {
            throw new ClientException("인증 문자를 요청해주세요");
        } else if (sms.getVerificationTime().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new ClientException("인증번호가 만료되었습니다. 인증번호 재발급 후 시도해주세요");
        } else if (!request.getCode().equals(sms.getCode())) {
            throw new ClientException("인증정보가 일치하지 않습니다");
        } else if (VerificationType.SIGN_UP.equals(request.getType())) {
            if (nonNull(user) || !VerificationType.SIGN_UP.equals(sms.getVerificationType())) {
                throw new ClientException("이미 가입된 정보가 있습니다.");
            }
        } else if (VerificationType.FIND_ID.equals(request.getType()) || VerificationType.FIND_PW.equals(request.getType())) {
            if (isNull(user)) {
                throw new ClientException("가입정보가 확인되지 않습니다.");
            }
        } else {
            throw new ClientException("잘못된 요청입니다.");
        }

        return Map.of(
                "message", "본인인증이 완료되었습니다."
        );
    }

    @Transactional
    public Map<String, Object> userSignUp(UserSignUpRequest request) {
        User user = userRepository.findByUserIdOrNicknameOrPhone(request.getUserId(), request.getNickname(), request.getPhone());
        if (nonNull(user)) {
            throw new ClientException("이미 존재하는 회원정보입니다.");
        }

        SmsVerification sms = smsVerificationRepository.findByPhone(request.getPhone());
        if (isNull(sms) || sms.getState()) {
            throw new ClientException("휴대폰 인증정보에 오류가 있습니다.");
        }

        User newMember = User.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .pw(request.getPassword())
                .country(request.getCountry())
                .ip(request.getIp())
                .build();
        userRepository.save(newMember);

        sms.setState(true);
        sms.setVerificationTime(LocalDateTime.now());
        smsVerificationRepository.save(sms);

        return Map.of(
                "message", "회원가입이 완료되었습니다."
        );
    }

    public Map<String, Object> findUserId(FindUserIdRequest request) {
        User user = userRepository.findByNameAndPhone(request.getName(), request.getPhone())
                .orElseThrow(() -> new ClientException("가입하신 정보가 확인되지 않습니다."));

        SmsVerification sms = smsVerificationRepository.findByPhoneAndName(request.getPhone(), request.getName())
                .orElseThrow(() -> new ClientException("인증 정보가 확인되지 않습니다."));
        if (sms.getState() || !VerificationType.FIND_ID.equals(sms.getVerificationType())) {
            throw new ClientException("휴대폰 인증에 오류가 있습니다.");
        } else if (sms.getVerificationTime().isBefore(LocalDateTime.now().minusMinutes(10))) {
            throw new ClientException("인증시간이 만료되었습니다. 재인증 후 시도해주세요");
        }

        sms.setState(true);
        sms.setVerificationTime(LocalDateTime.now());
        smsVerificationRepository.save(sms);

        return Map.of(
                "userId", user.getUserId()
        );
    }

    @Transactional
    public Map<String, Object> findPassword(FindUserPwRequest request) {
        User user = userRepository.findByUserIdAndNameAndPhone(request.getUserId(), request.getName(), request.getPhone())
                .orElseThrow(() -> new ClientException("가입하신 정보가 확인되지 않습니다."));

        SmsVerification sms = smsVerificationRepository.findByPhone(request.getPhone());
        if (isNull(sms) || sms.getState() || !VerificationType.FIND_PW.equals(sms.getVerificationType())) {
            throw new ClientException("휴대폰 인증에 오류가 있습니다.");
        } else if (sms.getVerificationTime().isBefore(LocalDateTime.now().minusMinutes(10))) {
            throw new ClientException("인증시간이 만료되었습니다. 재인증 후 시도해주세요");
        }

        int tempPwLength = 10;
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder tempPw = new StringBuilder(tempPwLength);

        for (int i = 0; i < tempPwLength; i++) {
            int index = random.nextInt(CHARACTERS.length());
            tempPw.append(CHARACTERS.charAt(index));
        }

        sms.setState(true);
        sms.setVerificationTime(LocalDateTime.now());
        smsVerificationRepository.save(sms);

        user.setPw(tempPw.toString());
        userRepository.save(user);

        return Map.of(
                "tempPw", tempPw,
                "message", "임시 비밀번호로 로그인 후 비밀번호를 재설정해주세요"
        );
    }

    public Map<String, Object> resetUserPassword(LoginInfo loginInfo, ResetUserPwRequest request) {
        User user = checkUser(loginInfo.getUserId());

        if (!user.getPw().equals(request.getOriginalPw())) {
            throw new ClientException("기존 비밀번호 정보가 일치하지 않습니다");
        } else if (request.getOriginalPw().equals(request.getNewPw())) {
            throw new ClientException("새 비밀번호는 기존과 다르게 설정해주세요");
        }

        user.setPw(request.getNewPw());
        userRepository.save(user);

        return Map.of(
                "message", "비밀번호가 재설정되었습니다."
        );
    }

    public Map<String, Object> userLeave(LoginInfo loginInfo) {
        User user = checkUser(loginInfo.getUserId());

        user.setRole(Role.WITHDRAWAL.name());
        userRepository.save(user);

        return Map.of(
                "message", "탈퇴처리 되었습니다."
        );
    }
}
