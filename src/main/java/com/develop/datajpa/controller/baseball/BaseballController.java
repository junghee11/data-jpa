package com.develop.datajpa.controller.baseball;


import com.develop.datajpa.entity.MatchType.TeamType;
import com.develop.datajpa.request.article.GetArticleListRequest;
import com.develop.datajpa.service.article.ArticleService;
import com.develop.datajpa.service.baseball.BaseballService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/baseball")
public class BaseballController {

    private final BaseballService baseballService;

    @GetMapping("/schedule/{team}")
    public Map<String, Object> getMatchList(@PathVariable TeamType team) {
        return baseballService.getMatchList(team.name());
    }

}
