package com.develop.datajpa.dto.shop;

import com.develop.datajpa.entity.shop.OrderMenu;
import com.develop.datajpa.entity.shop.Receipt;
import lombok.Data;

@Data
public class OrderDto {

    private String receiptCode;

    private String userId;

    private String payment;

    private String orderMenuCode;

    private String goodsCode;

    private Long price;

    private Integer count;

    private Boolean review;

    public OrderDto(Receipt receipt, OrderMenu orderMenu) {
        this.receiptCode = receipt.getReceiptCode();
        this.userId = receipt.getUserId();
        this.payment = receipt.getPayment();
        this.orderMenuCode = orderMenu.getOrderMenuCode();
        this.goodsCode = orderMenu.getGoodsCode();
        this.price = orderMenu.getPrice();
        this.count = orderMenu.getCount();
        this.review = orderMenu.getReview();
    }

}
