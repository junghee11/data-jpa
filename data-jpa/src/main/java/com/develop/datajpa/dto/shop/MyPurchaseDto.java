package com.develop.datajpa.dto.shop;

import com.develop.datajpa.entity.shop.Goods;
import com.develop.datajpa.entity.shop.OrderMenu;
import lombok.Data;

@Data
public class MyPurchaseDto {

    private String goodsCode;

    private String name;

    private Integer count;

    private String team;

    private String description;

    private Long price;

    private Double star;

    private Integer stock;

    private boolean onSale;

    public MyPurchaseDto(Goods goods, OrderMenu orderMenu) {
        this.goodsCode = goods.getGoodsCode();
        this.name = goods.getName();
        this.count = orderMenu.getCount();
        this.team = goods.getTeam();
        this.description = goods.getDescription();
        this.price = orderMenu.getPrice();
        this.star = goods.getStar();
        this.stock = goods.getStock();
        this.onSale = goods.isOnSale();
    }

}
