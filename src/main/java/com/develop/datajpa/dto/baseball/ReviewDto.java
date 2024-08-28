package com.develop.datajpa.dto.baseball;

import com.develop.datajpa.entity.Review;
import com.develop.datajpa.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDto {

    private Integer idx;

    private Integer restaurantsId;

    private Integer star;

    private String content;

    private String userId;

    private Integer state;

    private LocalDateTime createdAt;

    private String nickname;

    public ReviewDto(Review review, User user) {
        this.idx = review.getIdx();
        this.restaurantsId = review.getRestaurantsId();
        this.star = review.getStar();
        this.content = review.getContent();
        this.userId = review.getUserId();
        this.state = review.getState();
        this.createdAt = review.getCreatedAt();
        this.nickname = user.getNickname();
    }

}
