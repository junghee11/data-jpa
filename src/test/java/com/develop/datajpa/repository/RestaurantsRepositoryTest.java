package com.develop.datajpa.repository;

import com.develop.datajpa.entity.Article;
import com.develop.datajpa.entity.ArticleType;
import com.develop.datajpa.entity.baseball.Restaurants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RestaurantsRepositoryTest {

    @Autowired
    RestaurantsRepository restaurantsRepository;

    private String name;
    private Long stadium;
    private boolean inside;
    private String address;
    private String phone;
    private String openingHours;
    private String website;
    private String imgUrl;

    @BeforeEach
    void setUp() {
        name = "신통치킨";
        stadium = 3L;
        inside = true;
        address = "사랑시 고백구 행복동 123";
        phone = "010-0000-0000";
        openingHours = "15:00 - 21:00";
        website = "www.abc.com";
        imgUrl = "img1.jpg";
    }

    @DisplayName("식당 생성- success")
    @Test
    public void createRestaurantSuccess() throws Exception {
        Restaurants restaurant = Restaurants.builder()
            .name(name)
            .stadium(stadium)
            .inside(inside)
            .address(address)
            .phone(phone)
            .openingHours(openingHours)
            .website(website)
            .imgUrl(imgUrl)
            .build();

        Restaurants newRestaurant = restaurantsRepository.save(restaurant);

        assertThat(newRestaurant).isNotNull();
        assertThat(newRestaurant).isInstanceOf(Article.class);

        Optional<Restaurants> findRestaurant = restaurantsRepository.findById(newRestaurant.getIdx());

        assertThat(findRestaurant.get()).isNotNull();
        assertThat(findRestaurant.get()).isEqualTo(newRestaurant);
    }

    @DisplayName("식당 생성 - fail")
    @Test
    public void createRestaurantFail() throws Exception {
        Restaurants restaurant = Restaurants.builder()
//            .name(name)
            .stadium(stadium)
            .inside(inside)
            .address(address)
            .phone(phone)
            .openingHours(openingHours)
            .website(website)
            .imgUrl(imgUrl)
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            restaurantsRepository.save(restaurant);
        }, "중복된 제목으로 삽입 시 DataIntegrityViolationException이 발생해야 합니다.");
    }

}
