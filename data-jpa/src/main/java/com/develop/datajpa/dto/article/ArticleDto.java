package com.develop.datajpa.dto.article;

import com.develop.datajpa.entity.article.Article;
import com.develop.datajpa.entity.user.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDto {

    private long idx;

    private String category;

    private String title;

    private String content;

    private int state;

    private int viewCount;

    private int commentCount;

    private LocalDateTime createdAt;

    private String userId;

    private String name;

    private String nickname;

    private Integer grade;

    private String profileImgUrl;

    public ArticleDto(Article article, User user) {
        this.idx = article.getIdx();
        this.category = article.getCategory();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.state = article.getState();
        this.viewCount = article.getViewCount();
        this.commentCount = article.getCommentCount();
        this.createdAt = article.getCreatedAt();
        this.userId = article.getUserId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.grade = user.getGrade();
        this.profileImgUrl = user.getProfileImgUrl();
    }

}
