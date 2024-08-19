package com.develop.datajpa.request.baseball;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetStadiumInfoRequest {

    private String type;

    @NotBlank(message = "검색어를 입력해주세요")
    private String keyword;

}
