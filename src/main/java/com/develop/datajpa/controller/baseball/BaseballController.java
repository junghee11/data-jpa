package com.develop.datajpa.controller.baseball;


import com.develop.datajpa.entity.MatchType.TeamCode;
import com.develop.datajpa.request.baseball.GetPlayerInfoRequest;
import com.develop.datajpa.request.baseball.GetStadiumInfoRequest;
import com.develop.datajpa.request.baseball.LeaveReviewRequest;
import com.develop.datajpa.service.baseball.BaseballService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.develop.datajpa.request.valid.InvalidRequest.containsInvalidKeyword;
import static com.develop.datajpa.service.security.JwtProvider.resolveToken;

@RequiredArgsConstructor
@RestController
@RequestMapping("/baseball")
public class BaseballController {

    private final BaseballService baseballService;

    @GetMapping("/schedule/{team}")
    public Map<String, Object> getMatchList(@PathVariable("team") TeamCode team) {
        return baseballService.getMatchList(team);
    }

    @GetMapping("/team/{team}")
    public Map<String, Object> getTeamInfo(@PathVariable("team") TeamCode team) {
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

    @GetMapping("/restaurant/{id}")
    public Map<String, Object> getRestaurantInfo(@PathVariable("id") int restaurantId) {
        return baseballService.getRestaurantInfo(restaurantId);
    }

    @PostMapping("/restaurant/review")
    public Map<String, Object> leaveReview(@RequestHeader(value = "Authorization") String token,
                                           @RequestBody LeaveReviewRequest request) {
        return baseballService.leaveReview(resolveToken(token), request);
    }

    @DeleteMapping("restaurant/review/{id}")
    public Map<String, Object> deleteReview(@RequestHeader(value = "Authorization") String token,
                                            @PathVariable("id") long id) {
        return baseballService.deleteReview(resolveToken(token), id);
    }

}
