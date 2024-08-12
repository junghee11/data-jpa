package com.develop.datajpa.controller.baseball;


import com.develop.datajpa.entity.MatchType.TeamType;
import com.develop.datajpa.request.baseball.GetPlayerInfoRequest;
import com.develop.datajpa.request.baseball.GetStadiumInfoRequest;
import com.develop.datajpa.service.baseball.BaseballService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.develop.datajpa.request.valid.InvalidRequest.containsInvalidKeyword;

@RequiredArgsConstructor
@RestController
@RequestMapping("/baseball")
public class BaseballController {

    private final BaseballService baseballService;

    @GetMapping("/schedule/{team}")
    public Map<String, Object> getMatchList(@PathVariable TeamType team) {
        return baseballService.getMatchList(team);
    }

    @GetMapping("/team/{team}")
    public Map<String, Object> getTeamInfo(@PathVariable TeamType team) {
        return baseballService.getTeamInfo(team);
    }

    @GetMapping("/stadium")
    public Map<String, Object> getStadiumInfo(@Valid GetStadiumInfoRequest request) {
        containsInvalidKeyword(request.getKeyword());
        return baseballService.getStadiumInfo(request);
    }

    @GetMapping("/player")
    public Map<String, Object> getPlayerInfo(@Valid GetPlayerInfoRequest request) {
        containsInvalidKeyword(request.getKeyword());
        return baseballService.getPlayerInfo(request);
    }

}
