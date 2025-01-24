package com.develop.datajpa.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CheckUserPhoneRequest {

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @NotBlank(message = "전화번호를 입력해주세요")
    @Pattern(regexp = "[0-9]{10,11}$", message = "전화번호는 '-'를 제외하고 입력해주세요")
    private String phone;

}
