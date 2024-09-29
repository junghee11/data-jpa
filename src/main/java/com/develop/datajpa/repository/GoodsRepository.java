package com.develop.datajpa.repository;

import com.develop.datajpa.entity.Goods;
import com.develop.datajpa.entity.MatchType.TeamCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {

    Optional<Goods> findByIdxAndOnSaleOrderByIdx(long idx, boolean sale);

    List<Goods> findByTeamAndOnSaleOrderByIdx(TeamCode teamCode, boolean sale);

}
