package com.develop.datajpa.repository;

import com.develop.datajpa.entity.Player;
import com.develop.datajpa.entity.Stadium;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Page<Player> findAll(Pageable pageable);

    Optional<Player> findByNameLike(String name);

    List<Player> findByTeamLike(String team);

}
