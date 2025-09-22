package com.develop.domain.repository.baseball;

import com.develop.domain.entity.baseball.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByRestaurantsId(long id);

}
