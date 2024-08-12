package com.develop.datajpa.repository;

import com.develop.datajpa.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    Optional<Stadium> findByNameLike(String name);

    Optional<Stadium> findByTeamLike(String team);

    Optional<Stadium> findByAddressLike(String address);

}
