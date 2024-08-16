package com.develop.datajpa.repository;

import com.develop.datajpa.entity.MatchSchedule;
import com.develop.datajpa.entity.MatchType.MatchResult;
import com.develop.datajpa.entity.MatchType.TeamCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchScheduleRepository extends JpaRepository<MatchSchedule, Long> {

    List<MatchSchedule> findByMatchDate(LocalDate date);

    Optional<MatchSchedule> findByIdxAndMatchResult(Long id, MatchResult result);

    List<MatchSchedule> findByHomeTeamOrAwayTeamOrderByMatchDate(TeamCode home, TeamCode away);

}
