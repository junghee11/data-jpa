package com.develop.datajpa.repository;

import com.develop.datajpa.entity.MatchSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MatchScheduleRepository extends JpaRepository<MatchSchedule, Long> {

    List<MatchSchedule> findByMatchDate(Date date);

    List<MatchSchedule> findByHomeTeamOrAwayTeamOrderByMatchDate(String home, String away);

}
