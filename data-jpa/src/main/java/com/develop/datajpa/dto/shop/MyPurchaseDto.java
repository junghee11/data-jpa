package com.develop.datajpa.dto.shop;

import com.develop.datajpa.entity.shop.Goods;
import com.develop.datajpa.entity.shop.OrderMenu;
import lombok.Data;

@Data
public class MyPurchaseDto {

    private String goodsCode;

    private String name;

    private Integer count;

    private String imgUrl;

    private Long price;

    public MyPurchaseDto(Goods goods, OrderMenu orderMenu) {
        this.goodsCode = goods.getGoodsCode();
        this.name = orderMenu.getGoodsName();
        this.count = orderMenu.getCount();
        this.imgUrl = goods.getImgUrl();
        this.price = orderMenu.getPrice();
    }

}
