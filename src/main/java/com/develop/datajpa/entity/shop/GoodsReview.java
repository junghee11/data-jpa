package com.develop.datajpa.entity.shop;

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
@Table(name = "goods_review")
public class GoodsReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "goods_id")
    private Long goodsId;

    private Integer star;

    private String content;

    @Column(name = "user_id")
    private String userId;

    private Integer state;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void setStar(Integer star) {
        this.star = star;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Builder
    public GoodsReview(Long goodsId, Integer star, String content, String userId) {
        this.goodsId = goodsId;
        this.star = star;
        this.content = content;
        this.userId = userId;
    }
}
