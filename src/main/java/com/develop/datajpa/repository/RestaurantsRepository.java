package com.develop.datajpa.repository;

import com.develop.datajpa.entity.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantsRepository extends JpaRepository<Restaurants, Long> {

    List<Restaurants> findByStadium(int id);

}
