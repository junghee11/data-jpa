package com.develop.datajpa.repository.shop;

import com.develop.datajpa.entity.shop.GoodsReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsReviewRepository extends JpaRepository<GoodsReview, Long> {

}
