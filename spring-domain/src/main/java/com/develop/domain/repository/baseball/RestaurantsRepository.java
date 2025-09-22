package com.develop.domain.repository.baseball;

import com.develop.domain.entity.baseball.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantsRepository extends JpaRepository<Restaurants, Long> {

    List<Restaurants> findByStadium(int id);

}
