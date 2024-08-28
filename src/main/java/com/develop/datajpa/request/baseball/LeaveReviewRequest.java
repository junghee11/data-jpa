package com.develop.datajpa.request.baseball;

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
public class LeaveReviewRequest {

    @NotNull(message = "리뷰하실 식당을 확인해주세요")
    private int id;

    @NotNull(message = "평점을 입력해주세요")
    @Min(value = 1, message = "평점은 최소 1점 이상이여야 합니다")
    @Max(value = 5, message = "평점은 5점이 최대입니다")
    private int star;

    @NotBlank(message = "리뷰 내용을 작성해주세요")
    private String content;

}
