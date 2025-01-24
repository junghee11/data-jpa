package com.develop.datajpa.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResetUserPwRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    private String userId;

    @NotBlank(message = "잘못된 요청입니다")
    private String tempPw;

    @NotBlank(message = "재설정하실 비밀번호를 입력해주세요")
    private String newPw;

}
