package com.develop.datajpa.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CheckUserIdRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9]{4,10}$", message = "아이디는 영문(필수), 숫자(선택) 조합(공백제외) 4 ~ 10자로 설정해주세요")
    private String memberId;

}
