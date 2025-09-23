package com.develop.domain.entity.article;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DynamicInsert
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_idx")
    private long idx;

    private String category;

    private String title;

    private String content;

    private int state;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "comment_count")
    private int commentCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void addViewCount() {
        this.viewCount++;
    }

    public void addCommentCount() {
        this.commentCount++;
    }

    @Column(name = "user_id")
    private String userId;

    @Builder
    public Article(String category, String title, String content, String userId) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.userId = userId;
    }
}
