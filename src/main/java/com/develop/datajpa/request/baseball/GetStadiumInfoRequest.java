package com.develop.datajpa.request.baseball;

import com.develop.datajpa.entity.ArticleType.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetStadiumInfoRequest {

    private String type;

    @NotBlank(message = "검색어를 선택해주세요")
    private String keyword;

}
