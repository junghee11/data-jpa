package com.develop.datajpa.request.mypage;

import com.develop.domain.entity.article.ArticleType.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetChatMessageListRequest {

    @NotBlank(message = "채팅방을 선택해주세요")
    private String roomId;

    @Min(value = 1, message = "페이지값은 1보다 작을 수 없습니다")
    @NotNull(message = "페이지 번호를 확인해주세요")
    private int page;

}
