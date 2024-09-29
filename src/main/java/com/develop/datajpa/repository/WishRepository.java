package com.develop.datajpa.repository;

import com.develop.datajpa.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByGoodsIdxAndUserId(long idx, String userId);

}
