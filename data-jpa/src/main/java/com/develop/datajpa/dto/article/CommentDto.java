package com.develop.datajpa.dto.article;

import com.develop.datajpa.entity.User;
import com.develop.datajpa.entity.article.Comment;
import com.develop.datajpa.entity.article.CommentRecommend;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Data
@NoArgsConstructor
public class CommentDto {

    private long idx;

    private String content;

    private String userId;

    private Integer depth;

    private Integer state;

    private Integer up;

    private Integer down;

    private Boolean myUp;

    private Boolean myDown;

    private Integer commentCount;

    private LocalDateTime createdAt;

    private String nickname;

    private String profileImgUrl;

    private Integer grade;

    @Builder
    public CommentDto(Comment comment, User user, CommentRecommend recommend) {
        this.idx = comment.getIdx();
        this.content = comment.getContent();
        this.userId = comment.getUserId();
        this.depth = comment.getDepth();
        this.state = comment.getState();
        this.up = comment.getUp();
        this.down = comment.getDown();
        this.myUp = nonNull(recommend) ? recommend.getUp() : false;
        this.myDown = nonNull(recommend) ? recommend.getDown() : false;
        this.commentCount = comment.getCommentCount();
        this.createdAt = comment.getCreatedAt();
        this.nickname = user.getNickname();
        this.profileImgUrl = user.getProfileImgUrl();
        this.grade = user.getGrade();
    }

}
