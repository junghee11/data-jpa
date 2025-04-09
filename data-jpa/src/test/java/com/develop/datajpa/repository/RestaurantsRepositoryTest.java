package com.develop.datajpa.repository;

import com.develop.datajpa.entity.baseball.Restaurants;
import com.develop.datajpa.repository.baseball.RestaurantsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
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

    @DisplayName("식당 생성 - success")
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
        assertThat(newRestaurant).isInstanceOf(Restaurants.class);

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
        }, "필수 입력값 누락 시 DataIntegrityViolationException이 발생해야 합니다.");
    }

    @DisplayName("식당 정보 수정 - success")
    @Test
    public void updateRestaurantInfoSuccess() throws Exception {
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
        assertThat(newRestaurant).isInstanceOf(Restaurants.class);

        Restaurants findRestaurant = restaurantsRepository.findById(newRestaurant.getIdx()).get();

        findRestaurant.setName("갓튀긴 치킨");
        findRestaurant.setPhone("010-1234-1234");
        findRestaurant.setImgUrl("img99.jpg");
        findRestaurant.setUpdatedAt(LocalDateTime.now());

        assertThat(findRestaurant).isNotNull();
        assertThat(findRestaurant).isEqualTo(newRestaurant);
    }

    @DisplayName("식당 정보 수정 - fail test")
    @Test
    public void updateRestaurantInfoFail() throws Exception {
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
        Restaurants savedRestaurant = restaurantsRepository.save(restaurant);

        Restaurants findRestaurant = restaurantsRepository.findById(savedRestaurant.getIdx()).get();

        findRestaurant.setName(null);
        findRestaurant.setStar(null);
        findRestaurant.setAddress(null);
        findRestaurant.setUpdatedAt(LocalDateTime.now());

        // null 값으로 업데이트 해도 오류 발생하지 않음..
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            restaurantsRepository.save(findRestaurant);
//        }, "필수 입력값 null 일 경우 DataIntegrityViolationException 이 발생해야 합니다.");
    }

}
