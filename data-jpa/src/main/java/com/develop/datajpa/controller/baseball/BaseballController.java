package com.develop.datajpa.controller.baseball;


import com.develop.core.security.jwt.CustomUserDetails;
import com.develop.datajpa.request.baseball.GetMatchListRequest;
import com.develop.datajpa.request.baseball.GetPlayerInfoRequest;
import com.develop.datajpa.request.baseball.GetStadiumInfoRequest;
import com.develop.datajpa.request.baseball.LeaveReviewRequest;
import com.develop.datajpa.service.baseball.BaseballService;
import com.develop.domain.entity.baseball.MatchType.TeamCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.develop.datajpa.request.valid.InvalidRequest.containsInvalidKeyword;

@RequiredArgsConstructor
@RestController
@RequestMapping("/baseball")
public class BaseballController {

    private final BaseballService baseballService;

    @GetMapping("/schedule/{team}")
    public Map<String, Object> getMatchList(@PathVariable("team") TeamCode team,
                                            GetMatchListRequest request) {
        return baseballService.getMatchList(team, request.getDate());
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
    public Map<String, Object> getRestaurantInfo(@PathVariable("id") long restaurantId) {
        return baseballService.getRestaurantInfo(restaurantId);
    }

    @PostMapping("/restaurant/review")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> leaveReview(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @Valid @RequestBody LeaveReviewRequest request) {
        return baseballService.leaveReview(userDetails.getLoginInfo(), request);
    }

    @DeleteMapping("restaurant/review/{id}")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> deleteReview(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable("id") long id) {
        return baseballService.deleteReview(userDetails.getLoginInfo(), id);
    }

    @GetMapping("/restaurant/food/{id}")
    public Map<String, Object> getFoodInfo(@PathVariable("id") long foodId) {
        return baseballService.getFoodInfo(foodId);
    }

}
