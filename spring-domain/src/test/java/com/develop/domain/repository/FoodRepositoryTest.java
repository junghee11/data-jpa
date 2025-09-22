package com.develop.domain.repository;

import com.develop.domain.entity.baseball.Food;
import com.develop.domain.repository.baseball.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FoodRepositoryTest {

    @Autowired
    FoodRepository foodRepository;

    private long restaurantsId;
    private String name;
    private String description;
    private int price;
    private String imgUrl;

    @BeforeEach
    void setUp() {
        restaurantsId = 1;
        name = "국물 떡볶이";
        description = "국물 떡볶이, 학교앞에서 먹던 추억의 맛";
        price = 5000;
        imgUrl = "img1.jpg";
    }

    @DisplayName("음식 메뉴 생성- success")
    @Test
    public void createFoodSuccess() throws Exception {
        Food food = Food.builder()
            .restaurantsId(restaurantsId)
            .name(name)
            .description(description)
            .price(price)
            .imgUrl(imgUrl)
            .build();

        Food newFood = foodRepository.save(food);

        assertThat(newFood).isNotNull();
        assertThat(newFood).isInstanceOf(Food.class);

        Optional<Food> findFood = foodRepository.findById(newFood.getIdx());

        assertThat(findFood.get()).isNotNull();
        assertThat(findFood.get()).isEqualTo(newFood);
    }

    @DisplayName("음식 메뉴 생성 - fail")
    @Test
    public void createFoodFail() throws Exception {
        Food food = Food.builder()
//            .restaurantsId(restaurantsId)
            .name(name)
            .description(description)
            .price(price)
            .imgUrl(imgUrl)
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            foodRepository.save(food);
        }, "필수 입력값 누락 시 DataIntegrityViolationException 이 발생해야 합니다.");
    }

    @DisplayName("음식 메뉴 수정- success")
    @Test
    public void updateFoodSuccess() throws Exception {
        Food food = Food.builder()
            .restaurantsId(restaurantsId)
            .name(name)
            .description(description)
            .price(price)
            .imgUrl(imgUrl)
            .build();

        Food newFood = foodRepository.save(food);

        assertThat(newFood).isNotNull();
        assertThat(newFood).isInstanceOf(Food.class);

        Food findFood = foodRepository.findById(newFood.getIdx()).get();

        findFood.setName("국물이 끝내줘요~! 우리 가게 best 메뉴");
        findFood.setPrice(6000);
        findFood.setDescription("국물이 끝내줘요~! 우리 가게 best 메뉴");
        findFood.setImgUrl("img50.jpg");
        findFood.setUpdatedAt(LocalDateTime.now());

        Food updatedFood = foodRepository.save(findFood);

        assertThat(updatedFood).isEqualTo(newFood);
        assertThat(updatedFood.getDescription()).isNotEqualTo(description);
    }

    @DisplayName("음식 메뉴 수정 - fail")
    @Test
    public void updateFoodFail() throws Exception {
        Food food = Food.builder()
            .restaurantsId(restaurantsId)
            .name(name)
            .description(description)
            .price(price)
            .imgUrl(imgUrl)
            .build();

        Food savedFood = foodRepository.save(food);

        assertThat(savedFood).isNotNull();
        assertThat(savedFood).isInstanceOf(Food.class);

        Runnable task1 = () -> {
            foodRepository.findById(savedFood.getIdx()).ifPresent(f1 -> {
                f1.setPrice(21000);
                foodRepository.save(f1);
            });
        };

        Runnable task2 = () -> {
            foodRepository.findById(savedFood.getIdx()).ifPresent(f2 -> {
                f2.setPrice(22000);
                foodRepository.save(f2);
            });
        };

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // 최종 업데이트된 결과 확인
        Food updatedFood = foodRepository.findById(savedFood.getIdx()).orElseThrow();
        System.out.println("최종 가격: " + updatedFood.getPrice());
    }

    @DisplayName("음식 메뉴 삭제- success")
    @Test
    public void deleteFoodSuccess() throws Exception {
        Food food = Food.builder()
            .restaurantsId(restaurantsId)
            .name(name)
            .description(description)
            .price(price)
            .imgUrl(imgUrl)
            .build();

        Food newFood = foodRepository.save(food);

        assertThat(newFood).isNotNull();
        assertThat(newFood).isInstanceOf(Food.class);

        foodRepository.delete(newFood);

        Optional<Food> findFood = foodRepository.findById(newFood.getIdx());

        assertThat(findFood.isEmpty()).isTrue();
    }

    @DisplayName("음식 메뉴 삭제 - fail")
    @Test
    public void deleteFoodFail() throws Exception {
        Food food = Food.builder()
            .restaurantsId(restaurantsId)
            .name(name)
            .description(description)
            .price(price)
            .imgUrl(imgUrl)
            .build();

        Food newFood = foodRepository.save(food);

        assertThat(newFood).isNotNull();
        assertThat(newFood).isInstanceOf(Food.class);

        foodRepository.delete(newFood);

        Optional<Food> findFood = foodRepository.findById(newFood.getIdx());

        assertThrows(NoSuchElementException.class, () -> {
            findFood.get();
        }, "존재하지 않는 값 불러올 시 NoSuchElementException 이 발생해야 합니다.");
    }

}
