package com.develop.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString(of = {"idx", "title", "content", "user"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Article(String category, String title, String content, User user) {
        this.category = category;
        this.title = title;
        this.content = content;
        if (user != null) {
            changeWriter(user);
        }
    }

    public void changeWriter(User user) {
        this.user = user;
        user.getArticle().add(this);
    }
}
