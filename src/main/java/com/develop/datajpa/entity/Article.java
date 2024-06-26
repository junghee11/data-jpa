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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString(of = {"idx", "title", "content", "user"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_idx")
    private Integer idx;

    private String category;

    private String title;

    private String content;

    private Integer state;

    private Integer view_count;

    private Integer comment_count;

    private LocalDateTime created_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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
