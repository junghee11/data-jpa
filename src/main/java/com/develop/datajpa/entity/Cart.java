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
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "goods_idx")
    private Long goodsIdx;

    private Integer count;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void setCount(Integer count) {
        this.count = count;
    }

    @Builder
    public Cart(String userId, Long goodsIdx, Integer count) {
        this.userId = userId;
        this.goodsIdx = goodsIdx;
        this.count = count;
    }
}
