package com.develop.datajpa.request.baseball;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddCartRequest {

    @NotBlank(message = "상품정보가 확인되지 않습니다")
    private Long id;

    @NotBlank(message = "갯수를 확인해주세요")
    @Min(value = 1, message = "갯수가 1 이상이여야 합니다")
    @Max(value = 10, message = "동시구입 최대 갯수는 10개입니다")
    private Integer count;

}
