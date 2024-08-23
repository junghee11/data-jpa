package com.develop.datajpa.repository;

import com.develop.datajpa.entity.MatchType.TeamCode;
import com.develop.datajpa.entity.Team;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAll(Sort sort);

    Optional<Team> findByTeamCode(TeamCode name);

}
