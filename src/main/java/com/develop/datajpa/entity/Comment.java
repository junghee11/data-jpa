package com.develop.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_idx")
    private long idx;

    @Column(name = "article_idx")
    private long articleIdx;

    private String content;

    @Column(name = "user_id")
    private String userId;

    private Integer depth;

    private Integer state;

    private Integer up;

    private Integer down;

    @Column(name = "comment_count")
    private Integer commentCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
