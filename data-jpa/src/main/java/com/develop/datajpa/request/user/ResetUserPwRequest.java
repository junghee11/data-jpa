package com.develop.datajpa.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResetUserPwRequest {

    @NotBlank(message = "재설정하실 비밀번호를 입력해주세요")
    private String originalPw;

    @NotBlank(message = "재설정하실 비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$!%*?&])[A-Za-z\\d@#$!%*?&]{8,20}$", message = "비밀번호는 영문, 숫자, 특수문자 조합(공백제외) 8 ~ 20자로 설정해주세요")
    private String newPw;

}
