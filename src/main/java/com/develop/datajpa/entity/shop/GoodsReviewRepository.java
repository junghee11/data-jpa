package com.develop.datajpa.entity.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsReviewRepository extends JpaRepository<GoodsReview, Long> {

}
