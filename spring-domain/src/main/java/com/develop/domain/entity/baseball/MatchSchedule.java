package com.develop.domain.entity.baseball;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "match_schedule")
public class MatchSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "home_team")
    @Enumerated(EnumType.STRING)
    private MatchType.TeamCode homeTeam;

    @Column(name = "away_team")
    @Enumerated(EnumType.STRING)
    private MatchType.TeamCode awayTeam;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    private String stadium;

    @Column(name = "match_result")
    @Enumerated(EnumType.STRING)
    private MatchType.MatchResult matchResult;

    @Column(name = "match_date")
    private LocalDate matchDate;

    @Column(name = "match_time")
    private LocalTime matchTime;

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public void setMatchResult(MatchType.MatchResult matchResult) {
        this.matchResult = matchResult;
    }
}
