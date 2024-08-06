package com.develop.datajpa.service.baseball;

import com.develop.datajpa.entity.MatchSchedule;
import com.develop.datajpa.entity.MatchType.TeamType;
import com.develop.datajpa.repository.MatchScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BaseballService {

    private final MatchScheduleRepository matchScheduleRepository;

    public Map<String, Object> getMatchList(TeamType team) {
        List<MatchSchedule> matchSchedules;
        if (TeamType.ALL.equals(team)) {
            matchSchedules = matchScheduleRepository.findByMatchDate(LocalDate.now());
        } else {
            matchSchedules = matchScheduleRepository.findByHomeTeamOrAwayTeamOrderByMatchDate(team, team);
        }

        return Map.of(
            "result", matchSchedules
        );
    }
}
