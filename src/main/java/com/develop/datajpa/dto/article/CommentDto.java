package com.develop.datajpa.dto.article;

import com.develop.datajpa.entity.Comment;
import com.develop.datajpa.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private long idx;

    private String content;

    private String userId;

    private Integer depth;

    private Integer state;

    private Integer up;

    private Integer down;

    private Integer commentCount;

    private LocalDateTime createdAt;

    private String nickname;

    private String profileImgUrl;

    private Integer grade;

    @Builder
    public CommentDto(Comment comment, User user) {
        this.idx = comment.getIdx();
        this.content = comment.getContent();
        this.userId = comment.getUserId();
        this.depth = comment.getDepth();
        this.state = comment.getState();
        this.up = comment.getUp();
        this.down = comment.getDown();
        this.commentCount = comment.getCommentCount();
        this.createdAt = comment.getCreatedAt();
        this.nickname = user.getNickname();
        this.profileImgUrl = user.getProfileImgUrl();
        this.grade = user.getGrade();
    }

}
