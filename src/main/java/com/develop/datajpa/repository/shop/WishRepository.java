package com.develop.datajpa.repository.shop;

import com.develop.datajpa.entity.shop.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByGoodsCodeAndUserId(String goodsCode, String userId);

}
