package com.develop.datajpa.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CheckNicknameRequest {

    @NotBlank(message = "닉네임을 설정해주세요")
    @Pattern(regexp = "[A-Za-z가-힣0-9]{2,10}$", message = "닉네임은 영문 or 한글 or 숫자(공백제외)를 조합하여 2 ~ 10자로 설정해주세요")
    private String nickname;

}
