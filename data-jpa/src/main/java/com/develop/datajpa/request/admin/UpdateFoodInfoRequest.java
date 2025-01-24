package com.develop.datajpa.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateFoodInfoRequest {

    @NotNull(message = "메뉴 번호가 확인되지 않습니다.")
    private long id;

    @NotBlank(message = "요리 이름을 확인해주세요")
    private String name;

    @NotNull(message = "요리 가격을 확인해주세요")
    private int price;

    private String desc;

    private String imgUrl;

}
