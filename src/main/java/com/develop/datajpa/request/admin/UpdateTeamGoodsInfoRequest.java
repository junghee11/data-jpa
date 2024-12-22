package com.develop.datajpa.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateTeamGoodsInfoRequest {

    @NotBlank(message = "제품 번호가 확인되지 않습니다")
    private String id;

    @NotBlank(message = "제품 이름이 확인되지 않습니다.")
    private String name;

    @NotNull(message = "가격 정보가 확인되지 않습니다.")
    private Long price;

    private String description;

    @NotNull(message = "재고 갯수가 확인되지 않습니다.")
    private Integer stock;

    private String imgUrl;

    @NotNull(message = "할인율 정보가 확인되지 않습니다.")
    private Double discountRate;

    @NotNull(message = "포인트율 정보가 확인되지 않습니다.")
    private Double pointRate;

}
