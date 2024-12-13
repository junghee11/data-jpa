package com.develop.datajpa.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterRestaurantRequest {

    @NotBlank(message = "식당 이름을 확인해주세요")
    private String name;

    @NotNull(message = "경기장을 선택해주세요")
    private int stadium;

    @NotNull(message = "경기장 내/외부를 선택해주세요")
    private boolean inside;

    @NotBlank(message = "식당 주소를 확인해주세요")
    private String address;

    private String phone;

    private String openingHours;

    private String webSite;

    private String imgUrl;

}
