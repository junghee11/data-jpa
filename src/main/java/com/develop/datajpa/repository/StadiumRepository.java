package com.develop.datajpa.repository;

import com.develop.datajpa.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    Optional<Stadium> findByNameContaining(String name);

    Optional<List<Stadium>> findByTeamContaining(String team);

    Optional<List<Stadium>> findByAddressContaining(String address);

}
