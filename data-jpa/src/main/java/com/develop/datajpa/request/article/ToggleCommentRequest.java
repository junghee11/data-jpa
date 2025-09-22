package com.develop.datajpa.request.article;

import com.develop.domain.entity.article.ArticleType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToggleCommentRequest {

    @NotNull(message = "댓글이 확인되지 않습니다")
    private Long commentId;

    @NotNull(message = "추천 혹은 비추천을 선택해주세요")
    private ArticleType.Recommend recommend;

}
