package com.develop.datajpa.service.baseball;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.Article;
import com.develop.datajpa.entity.ArticleType.State;
import com.develop.datajpa.entity.MatchSchedule;
import com.develop.datajpa.entity.User;
import com.develop.datajpa.repository.ArticleRepository;
import com.develop.datajpa.repository.MatchScheduleRepository;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.request.article.GetArticleListRequest;
import com.develop.datajpa.request.article.ModifyArticleRequest;
import com.develop.datajpa.response.ClientException;
import com.develop.datajpa.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BaseballService {

    private final MatchScheduleRepository matchScheduleRepository;

    public Map<String, Object> getMatchList(String team) {
        List<MatchSchedule> matchSchedules = matchScheduleRepository.findByHomeTeamOrAwayTeamOrderByMatchDate(team, team);

        return Map.of(
            "result", matchSchedules
        );
    }
}
