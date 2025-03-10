package com.develop.datajpa.repository.shop;

import com.develop.datajpa.dto.shop.GoodsReviewStarDto;
import com.develop.datajpa.entity.shop.GoodsReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsReviewRepository extends JpaRepository<GoodsReview, Long> {

    @Query(value = "SELECT gr.goods_code AS goodsCode, TRUNC(AVG(gr.star), 1) AS star FROM goods_review gr GROUP BY gr.goods_code", nativeQuery = true)
    List<GoodsReviewStarDto> findAverageStarsByGoods();

}
