package com.develop.domain.entity.shop;

import jakarta.persistence.*;
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

    @Column(name = "goods_code")
    private String goodsCode;

    private Integer count;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void setCount(Integer count) {
        this.count = count;
    }

    @Builder
    public Cart(String userId, String goodsCode, Integer count) {
        this.userId = userId;
        this.goodsCode = goodsCode;
        this.count = count;
    }
}
