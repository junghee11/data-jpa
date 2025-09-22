package com.develop.datajpa.request.user;

import com.develop.domain.entity.user.SmsType.VerificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SendPhoneSmsRequest {

    @NotBlank(message = "전화번호를 입력해주세요")
    @Pattern(regexp = "[0-9]{10,11}$", message = "전화번호는 '-'를 제외하고 입력해주세요")
    private String phone;

    @NotBlank(message = "이름 입력해주세요")
    private String name;

    @NotNull(message = "인증타입을 확인해주세요")
    private VerificationType type;

}
