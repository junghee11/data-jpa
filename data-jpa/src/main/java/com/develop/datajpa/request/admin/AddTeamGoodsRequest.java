package com.develop.datajpa.request.admin;

import com.develop.domain.entity.baseball.MatchType.TeamCode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddTeamGoodsRequest {

    @NotBlank(message = "제품 이름이 확인되지 않습니다.")
    private String name;

    @NotNull(message = "팀 정보가 확인되지 않습니다.")
    private TeamCode team;

    @NotNull(message = "가격 정보가 확인되지 않습니다.")
    private Long price;

    private String description;

    @NotNull(message = "재고 갯수가 확인되지 않습니다.")
    private Integer stock;

    @NotNull(message = "판매 여부가 확인되지 않습니다.")
    private Boolean onSale;

    private String imgUrl;

    @Min(value = 0, message = "할인율은 0 미만이 될 수 없습니다.")
    @Max(value = 1, message = "할인율은 1 이하여야 합니다.")
    @NotNull(message = "할인율 정보가 확인되지 않습니다.")
    private Double discountRate;

    @Min(value = 0, message = "포인트 적립은 0 미만이 될 수 없습니다.")
    @Max(value = 1, message = "포인트 적립율은 1 이하여야 합니다.")
    @NotNull(message = "포인트 적립율 정보가 확인되지 않습니다.")
    private Double pointRate;

}
