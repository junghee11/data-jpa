package com.develop.datajpa.repository.shop;

import com.develop.datajpa.entity.shop.Goods;
import com.develop.datajpa.entity.shop.GoodsType.State;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {

    Optional<Goods> findByGoodsCodeAndOnSaleAndGoodsStateOrderByCreatedAt(String code, boolean sale, State state);

    List<Goods> findByOnSaleAndGoodsState(boolean sale, State state, Pageable pageable);

    List<Goods> findByOnSaleAndGoodsState(boolean sale, State state);

    List<Goods> findByTeamAndOnSaleAndGoodsState(String team, boolean sale, State state, Pageable pageable);

    Optional<Goods> findByGoodsCode(String code);

    Optional<Goods> findByGoodsCodeAndOnSaleAndGoodsState(String code, boolean sale, State state);

    List<Goods> findByGoodsCodeInAndGoodsState(String[] code,State state);

}
