package com.develop.datajpa.request.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ModifyArticleRequest {

    @NotNull(message = "수정할 게시글 번호를 확인해주세요")
    private long id;

    @NotBlank(message = "수정할 게시글 제목을 확인해주세요")
    private String title;

    @NotBlank(message = "수정할 게시글 내용을 확인해주세요")
    private String content;

}
