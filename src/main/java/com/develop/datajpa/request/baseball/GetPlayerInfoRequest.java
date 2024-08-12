package com.develop.datajpa.request.baseball;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetPlayerInfoRequest {

    private String type;

    @NotBlank(message = "검색어를 선택해주세요")
    private String keyword;

    @Min(value = 1, message = "페이지 값은 1보다 작을 수 없습니다")
    @NotNull(message = "페이지 정보를 확인해주세요")
    private int page = 1;

}
