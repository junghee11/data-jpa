package com.develop.domain.entity.shop;

import com.develop.core.exception.ClientException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "goods")
public class Goods {

    @Id
    @Column(name = "goods_code")
    private String goodsCode;

    private String name;

    private String team;

    private String description;

    private Long price;

    private Double star;

    private Integer stock;

    @Column(name = "on_sale")
    private boolean onSale;

    @Column(name = "goods_state", columnDefinition = "goods_state")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private GoodsType.State goodsState;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "discount_rate")
    private Double discountRate;

    @Column(name = "point_rate")
    private Double pointRate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStar(Double star) {
        this.star = star;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    public void setGoodsState(GoodsType.State goodsState) {
        this.goodsState = goodsState;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }

    public void setPointRate(Double pointRate) {
        this.pointRate = pointRate;
    }

    public void updateStock(Integer amount) {
        if (this.stock + amount < 0) {
            throw new ClientException("재고가 부족합니다");
        }

        this.stock += amount;
    }

    @Builder
    public Goods(String goodsCode, String name, String team, String description, Long price, Integer stock, boolean onSale,
                 String imgUrl, Double discountRate, Double pointRate) {
        this.goodsCode = goodsCode;
        this.name = name;
        this.team = team;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.onSale = onSale;
        this.imgUrl = imgUrl;
        this.discountRate = discountRate;
        this.pointRate = pointRate;
    }
}
