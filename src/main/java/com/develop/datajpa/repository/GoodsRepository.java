package com.develop.datajpa.repository;

import com.develop.datajpa.entity.shop.Goods;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {

    Optional<Goods> findByIdxAndOnSaleOrderByIdx(long idx, boolean sale);

    List<Goods> findByOnSale(boolean sale, Pageable pageable);

    List<Goods> findByTeamAndOnSale(String team, boolean sale, Pageable pageable);

}
