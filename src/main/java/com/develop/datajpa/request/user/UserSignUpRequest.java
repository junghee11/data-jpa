package com.develop.datajpa.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserSignUpRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9]{4,10}$", message = "아이디는 영문(필수), 숫자(선택) 조합(공백제외) 4 ~ 10자로 설정해주세요")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$!%*?&])[A-Za-z\\d@#$!%*?&]{8,20}$", message = "비밀번호는 영문, 숫자, 특수문자 조합(공백제외) 8 ~ 20자로 설정해주세요")
    private String password;

    @NotBlank(message = "성함을 입력해주세요")
    private String name;

    @NotBlank(message = "닉네임을 설정해주세요")
    @Pattern(regexp = "[A-Za-z가-힣0-9]{2,10}$", message = "닉네임은 영문 or 한글 or 숫자(공백제외)를 조합하여 2 ~ 10자로 설정해주세요")
    private String nickname;

    @NotBlank(message = "전화번호를 입력해주세요")
    @Pattern(regexp = "[0-9]{10,11}$", message = "전화번호는 '-'를 제외하고 입력해주세요")
    private String phone;

    @NotBlank(message = "정보가 확인되지 않습니다.")
    private String ip;

}
