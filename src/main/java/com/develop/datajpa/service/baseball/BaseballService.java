package com.develop.datajpa.service.baseball;

import com.develop.datajpa.dto.baseball.MatchDto;
import com.develop.datajpa.entity.MatchSchedule;
import com.develop.datajpa.entity.MatchType.TeamCode;
import com.develop.datajpa.entity.Player;
import com.develop.datajpa.repository.MatchScheduleRepository;
import com.develop.datajpa.repository.PlayerRepository;
import com.develop.datajpa.repository.StadiumRepository;
import com.develop.datajpa.repository.TeamRepository;
import com.develop.datajpa.request.baseball.GetPlayerInfoRequest;
import com.develop.datajpa.request.baseball.GetStadiumInfoRequest;
import com.develop.datajpa.response.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BaseballService {

    private final MatchScheduleRepository matchScheduleRepository;
    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;
    private final PlayerRepository playerRepository;

    public Map<String, Object> getMatchList(TeamCode team) {
        List<MatchSchedule> matchSchedules;
        if (TeamCode.ALL.equals(team)) {
            matchSchedules = matchScheduleRepository.findByMatchDate(LocalDate.now());
        } else {
            matchSchedules = matchScheduleRepository.findByHomeTeamOrAwayTeamOrderByMatchDate(team, team);
        }

        Map<String, String> imgList = new HashMap<String, String>();
        teamRepository.findAll().stream().forEach(teamInfo -> {
            imgList.put(teamInfo.getTeamCode().get(), teamInfo.getImgUrl());
        });

        List<MatchDto> result = matchSchedules.stream().map(schedule -> {
            MatchDto match = new MatchDto(schedule);
            match.setHomeImgUrl(imgList.get(schedule.getHomeTeam().name()));
            match.setAwayImgUrl(imgList.get(schedule.getAwayTeam().name()));

            return match;
        }).toList();

        return Map.of(
            "result", result
        );
    }

    public Map<String, Object> getTeamInfo(TeamCode team) {
        if (TeamCode.ALL.equals(team)) {
            return Map.of(
                "result", teamRepository.findAll()
            );
        } else {
            System.out.println("team = " + team);
            System.out.println("team = " + team.name());
            return Map.of(
                "result", teamRepository.findByTeamCode(team)
                    .orElseThrow(() -> new ClientException("조회되는 팀이 없습니다."))
            );
        }
    }

    public Map<String, Object> getStadiumInfo(GetStadiumInfoRequest request) {
        if ("all".equals(request.getKeyword())) {
            return Map.of(
                "result", stadiumRepository.findAll()
            );
        } else if ("name".equals(request.getType())) {
            return Map.of(
                "result", stadiumRepository.findByNameContaining(request.getKeyword())
                    .orElseThrow(() -> new ClientException("조회되는 경기장이 없습니다."))
            );
        } else if ("team".equals(request.getType())) {
            return Map.of(
                "result", stadiumRepository.findByTeamContaining(request.getKeyword())
                    .orElseThrow(() -> new ClientException("조회되는 경기장이 없습니다."))
            );
        } else if ("address".equals(request.getType())) {
            return Map.of(
                "result", stadiumRepository.findByAddressContaining(request.getKeyword())
                    .orElseThrow(() -> new ClientException("조회되는 경기장이 없습니다."))
            );
        } else {
            throw new ClientException("검색 조건을 확인해주세요");
        }
    }

    public Map<String, Object> getPlayerInfo(GetPlayerInfoRequest request) {
        if ("all".equals(request.getKeyword())) {
            Page<Player> players = playerRepository.findAll(PageRequest.of(request.getPage() - 1, 10,
                Sort.by("name").ascending()));
            return Map.of(
                "result", players.getContent(),
                "page", players.getTotalPages()
            );
        } else if ("name".equals(request.getType())) {
            return Map.of(
                "result", playerRepository.findByNameContaining(request.getKeyword())
                    .orElseThrow(() -> new ClientException("조회되는 선수가 없습니다."))
            );
        } else if ("team".equals(request.getType())) {
            List<Player> players = playerRepository.findByTeamContaining(request.getKeyword());
            return Map.of(
                "result", players
            );
        } else {
            throw new ClientException("검색 조건을 확인해주세요");
        }
    }

}
