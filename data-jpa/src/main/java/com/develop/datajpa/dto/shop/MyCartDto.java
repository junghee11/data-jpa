package com.develop.datajpa.dto.shop;

import com.develop.datajpa.entity.shop.Cart;
import com.develop.datajpa.entity.shop.Goods;
import com.develop.datajpa.entity.shop.OrderMenu;
import com.develop.datajpa.entity.shop.Receipt;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class MyCartDto {

    private String goodsCode;

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
        this.count = count;
        this.team = goods.getTeam();
        this.description = goods.getDescription();
        this.price = goods.getPrice();
        this.star = goods.getStar();
        this.stock = goods.getStock();
        this.onSale = goods.isOnSale();
    }

}
