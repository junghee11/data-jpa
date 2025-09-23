package com.develop.domain.repository.baseball;

import com.develop.domain.entity.baseball.MatchSchedule;
import com.develop.domain.entity.baseball.MatchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchScheduleRepository extends JpaRepository<MatchSchedule, Long> {

    List<MatchSchedule> findByMatchDate(LocalDate date);

    Optional<MatchSchedule> findByIdxAndMatchResult(Long id, MatchType.MatchResult result);

    @Query("SELECT m FROM MatchSchedule m " +
            "WHERE m.matchDate BETWEEN :startDate AND :endDate " +
            "AND (m.homeTeam = :team OR m.awayTeam = :team) ORDER BY m.matchDate asc")
    List<MatchSchedule> findByDateRangeAndTeam(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("team") MatchType.TeamCode team);

}
