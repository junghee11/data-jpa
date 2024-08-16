package com.develop.datajpa.dto.baseball;

import com.develop.datajpa.entity.MatchSchedule;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MatchDto {

    private Long idx;

    private String homeTeam;

    private String awayTeam;

    private Integer homeScore;

    private Integer awayScore;

    private String homeImgUrl;

    private String awayImgUrl;

    private String stadium;

    private String matchResult;

    private LocalDate matchDate;

    private LocalTime matchTime;

    public MatchDto(MatchSchedule schedule) {
        this.idx = schedule.getIdx();
        this.homeTeam = schedule.getHomeTeam().get();
        this.awayTeam = schedule.getAwayTeam().get();
        this.homeScore = schedule.getHomeScore();
        this.awayScore = schedule.getAwayScore();
        this.stadium = schedule.getStadium();
        this.matchResult = schedule.getMatchResult().get();
        this.matchDate = schedule.getMatchDate();
        this.matchTime = schedule.getMatchTime();
    }

}
