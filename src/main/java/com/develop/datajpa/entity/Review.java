package com.develop.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
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
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "restaurants_id")
    private Integer restaurantsId;

    private Integer star;

    private String content;

    @Column(name = "user_id")
    private String userId;

    private Integer state;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void setState(Integer state) {
        this.state = state;
    }

    @Builder
    public Review(Integer restaurantsId, Integer star, String content, String userId) {
        this.restaurantsId = restaurantsId;
        this.star = star;
        this.content = content;
        this.userId = userId;
    }
}
