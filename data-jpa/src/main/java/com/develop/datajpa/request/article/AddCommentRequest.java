package com.develop.datajpa.request.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddCommentRequest {

    @NotNull(message = "게시글이 확인되지 않습니다")
    private Long articleId;

    private Long commentId;

    @NotBlank(message = "댓글 내용을 작성해주세요")
    @Size(min = 10, max = 300, message = "댓글은 최소 10자 이상, 최대 300자 이하로 작성해주세요")
    private String content;

}
