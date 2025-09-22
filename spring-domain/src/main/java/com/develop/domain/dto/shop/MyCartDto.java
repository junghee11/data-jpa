package com.develop.domain.dto.shop;

import com.develop.domain.entity.shop.Goods;
import lombok.Data;

@Data
public class MyCartDto {

    private String goodsCode;

    private String imgUrl;

    private String name;

    private Integer count;

    private String team;

    private String description;

    private Long price;

    private Double star;

    private Integer stock;

    private boolean onSale;

    public MyCartDto(Goods goods, int count) {
        this.goodsCode = goods.getGoodsCode();
        this.name = goods.getName();
        this.imgUrl = goods.getImgUrl();
        this.count = count;
        this.team = goods.getTeam();
        this.description = goods.getDescription();
        this.price = goods.getPrice();
        this.star = goods.getStar();
        this.stock = goods.getStock();
        this.onSale = goods.isOnSale();
    }

}
