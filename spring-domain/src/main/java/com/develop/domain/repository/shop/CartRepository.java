package com.develop.domain.repository.shop;

import com.develop.domain.entity.shop.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserId(String userId);

    Optional<Cart> findByGoodsCode(String id);

}
