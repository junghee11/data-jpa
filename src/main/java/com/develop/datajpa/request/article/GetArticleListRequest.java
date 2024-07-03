package com.develop.datajpa.request.article;

import com.develop.datajpa.entity.ArticleType.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetArticleListRequest {

    @NotNull(message = "게시판 종류를 확인해주세요")
    private Category category;

    @Min(value = 1, message = "페이지값은 1보다 작을 수 없습니다")
    @NotNull(message = "페이지 번호를 확인해주세요")
    private int page;

}
