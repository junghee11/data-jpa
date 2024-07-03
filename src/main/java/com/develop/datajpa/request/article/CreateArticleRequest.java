package com.develop.datajpa.request.article;

import com.develop.datajpa.entity.ArticleType.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateArticleRequest {

    @NotNull(message = "게시판 종류를 확인해주세요")
    private Category category;

    @NotBlank(message = "게시글 제목을 확인해주세요")
    private String title;

    @NotBlank(message = "게시글 내용을 확인해주세요")
    private String content;

}
