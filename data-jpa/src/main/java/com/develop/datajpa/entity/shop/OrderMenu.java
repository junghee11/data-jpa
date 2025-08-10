package com.develop.datajpa.entity.shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "order_menu_code")
    private String orderMenuCode;

    @Column(name = "receipt_code")
    private String receiptCode;

    @Column(name = "goods_code")
    private String goodsCode;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "goods_name")
    private String goodsName;

    private Long price;

    private Integer count;

    private Boolean review;

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }

    @Builder
    public OrderMenu(String orderMenuCode, String goodsCode, String userId, String goodsName, Long price, Integer count) {
        this.orderMenuCode = orderMenuCode;
        this.goodsCode = goodsCode;
        this.userId = userId;
        this.goodsName = goodsName;
        this.price = price;
        this.count = count;
    }
}
