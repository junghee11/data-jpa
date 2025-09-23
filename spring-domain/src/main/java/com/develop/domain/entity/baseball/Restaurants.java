package com.develop.domain.entity.baseball;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "restaurants")
public class Restaurants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String name;

    private long stadium;

    private boolean inside;

    private Double star;

    private String address;

    private Integer up;

    private Integer down;

    private String phone;

    @Column(name = "opening_hours")
    private String openingHours;

    private String website;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void setName(String name) {
        this.name = name;
    }

    public void setStadium(long stadium) {
        this.stadium = stadium;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public void setStar(Double star) {
        this.star = star;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUp(Integer up) {
        this.up = up;
    }

    public void setDown(Integer down) {
        this.down = down;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Builder
    public Restaurants(String name, long stadium, boolean inside, String address, String phone, String openingHours,
                       String website, String imgUrl) {
        this.name = name;
        this.stadium = stadium;
        this.inside = inside;
        this.address = address;
        this.phone = phone;
        this.openingHours = openingHours;
        this.website = website;
        this.imgUrl = imgUrl;
    }
}
