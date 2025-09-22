package com.develop.domain.dto.shop;

import com.develop.domain.entity.shop.Goods;
import com.develop.domain.entity.shop.OrderMenu;
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
