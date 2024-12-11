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

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "order_menu")
public class OrderMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_menu_idx")
    private Long orderMenuIdx;

    @Column(name = "receipt_code")
    private String receiptCode;

    @Column(name = "good_idx")
    private Long goodIdx;

    private Long price;

    private Integer count;

    private Boolean review;

    @Builder
    public OrderMenu(Long goodIdx, Long price, Integer count) {
        this.goodIdx = goodIdx;
        this.price = price;
        this.count = count;
    }
}
