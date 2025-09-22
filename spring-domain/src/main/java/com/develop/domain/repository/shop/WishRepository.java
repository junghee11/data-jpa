package com.develop.domain.repository.shop;

import com.develop.domain.entity.shop.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByGoodsCodeAndUserId(String goodsCode, String userId);

    List<Wish> findByUserId(String userId);

}
