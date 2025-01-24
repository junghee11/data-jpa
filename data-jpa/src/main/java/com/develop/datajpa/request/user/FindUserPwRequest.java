package com.develop.datajpa.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindUserPwRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    private String userId;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @NotBlank(message = "휴대폰 번호를 입력해주세요")
    private String phone;

}
