package com.develop.datajpa.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddFoodMenuOnRestaurantRequest {

    @NotNull(message = "식당번호가 확인되지 않습니다.")
    private Long id;

    @NotBlank(message = "메뉴 이름이 확인되지 않습니다.")
    private String name;

    @NotNull(message = "가격 정보가 확인되지 않습니다.")
    private Integer price;

    private String desc;

    private String imgUrl;

}
