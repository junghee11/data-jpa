package com.develop.datajpa.service.baseball;

import com.develop.datajpa.entity.MatchSchedule;
import com.develop.datajpa.entity.MatchType.TeamType;
import com.develop.datajpa.entity.Player;
import com.develop.datajpa.entity.Team;
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
import java.util.List;
import java.util.Map;

import static com.develop.datajpa.request.valid.InvalidRequest.containsInvalidKeyword;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class BaseballService {

    private final MatchScheduleRepository matchScheduleRepository;
    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;
    private final PlayerRepository playerRepository;

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

    public Map<String, Object> getTeamInfo(TeamType team) {
        if (TeamType.ALL.equals(team)) {
            return Map.of(
                "result", teamRepository.findAll()
            );
        } else {
            return Map.of(
                "result", teamRepository.findByName(team.name())
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
                "result", stadiumRepository.findByNameLike(request.getKeyword())
                    .orElseThrow(() -> new ClientException("조회되는 경기장이 없습니다."))
            );
        } else if ("team".equals(request.getType())) {
            return Map.of(
                "result", stadiumRepository.findByTeamLike(request.getKeyword())
                    .orElseThrow(() -> new ClientException("조회되는 경기장이 없습니다."))
            );
        } else if ("address".equals(request.getType())) {
            return Map.of(
                "result", stadiumRepository.findByAddressLike(request.getKeyword())
                    .orElseThrow(() -> new ClientException("조회되는 경기장이 없습니다."))
            );
        } else {
            throw new ClientException("검색 조건을 확인해주세요");
        }
    }

    public Map<String, Object> getPlayerInfo(GetPlayerInfoRequest request) {
        if ("all".equals(request.getKeyword())) {
            Page<Player> players = playerRepository.findAll(PageRequest.of(request.getPage(), 10,
                Sort.by("name").ascending()));
            return Map.of(
                "result", players.getContent(),
                "page", players.getTotalPages()
            );
        } else if ("name".equals(request.getType())) {
            return Map.of(
                "result", playerRepository.findByNameLike(request.getKeyword())
                    .orElseThrow(() -> new ClientException("조회되는 선수가 없습니다."))
            );
        } else if ("team".equals(request.getType())) {
            List<Player> players = playerRepository.findByTeamLike(request.getKeyword());
            return Map.of(
                "result", players
            );
        }  else {
            throw new ClientException("검색 조건을 확인해주세요");
        }
    }

}
