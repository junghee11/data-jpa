package com.develop.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "goods")
public class Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String name;

    private String team;

    private String description;

    private String price;

    private Integer stock;

    @Column(name = "on_sale")
    private boolean onSale;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "discount_rate")
    private Double discountRate;

    @Column(name = "point_rate")
    private Double pointRate;


}
