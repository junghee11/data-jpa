package com.develop.datajpa.service.baseball;

import com.develop.datajpa.dto.baseball.MatchDto;
import com.develop.datajpa.dto.baseball.ReviewDto;
import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.Food;
import com.develop.datajpa.entity.MatchSchedule;
import com.develop.datajpa.entity.MatchType.TeamCode;
import com.develop.datajpa.entity.Player;
import com.develop.datajpa.entity.QReview;
import com.develop.datajpa.entity.QUser;
import com.develop.datajpa.entity.Restaurants;
import com.develop.datajpa.entity.Review;
import com.develop.datajpa.entity.ReviewRepository;
import com.develop.datajpa.repository.FoodRepository;
import com.develop.datajpa.repository.MatchScheduleRepository;
import com.develop.datajpa.repository.PlayerRepository;
import com.develop.datajpa.repository.RestaurantsRepository;
import com.develop.datajpa.repository.StadiumRepository;
import com.develop.datajpa.repository.TeamRepository;
import com.develop.datajpa.request.baseball.GetPlayerInfoRequest;
import com.develop.datajpa.request.baseball.GetStadiumInfoRequest;
import com.develop.datajpa.request.baseball.LeaveReviewRequest;
import com.develop.datajpa.response.ClientException;
import com.develop.datajpa.service.user.UserService;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseballService {

    private final MatchScheduleRepository matchScheduleRepository;
    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;
    private final PlayerRepository playerRepository;
    private final RestaurantsRepository restaurantsRepository;
    private final FoodRepository foodRepository;
    private final UserService userService;
    private final ReviewRepository reviewRepository;

    @Autowired
    EntityManager em;

    public Map<String, Object> getMatchList(TeamCode team) {
        List<MatchSchedule> matchSchedules;
        if (TeamCode.ALL.equals(team)) {
            matchSchedules = matchScheduleRepository.findByMatchDate(LocalDate.now());
        } else {
            matchSchedules = matchScheduleRepository.findByHomeTeamOrAwayTeamOrderByMatchDate(team, team);
        }

        Map<String, String> imgList = new HashMap<String, String>();
        teamRepository.findAll().stream().forEach(teamInfo -> {
            imgList.put(teamInfo.getTeamCode().name(), teamInfo.getImgUrl());
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
                "result", teamRepository.findAll(Sort.by("rank").ascending())
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
        } else if ("id".equals(request.getType())) {
            return Map.of(
                "result", stadiumRepository.findById(Long.parseLong(request.getKeyword()))
                    .orElseThrow(() -> new ClientException("조회되는 경기장이 없습니다.")),
                "restaurants", restaurantsRepository.findByStadium(Integer.parseInt(request.getKeyword()))
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

    public Map<String, Object> getRestaurantInfo(int id) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QReview r = QReview.review;
        QUser u = QUser.user;

        List<ReviewDto> reviews = queryFactory.select(
                Projections.constructor(ReviewDto.class,
                    r, u))
            .from(r)
            .join(u)
            .where(
                r.restaurantsId.eq(id)
                    .and(r.state.eq(0))
                    .and(u.state.eq(0)))
            .orderBy(r.createdAt.desc())
            .limit(10)
            .fetch();

        Restaurants restaurants = restaurantsRepository.findById((long) id)
            .orElseThrow(() -> new ClientException("조회되는 식당이 없습니다."));

        List<Food> food = foodRepository.findByRestaurantsId(id);

        return Map.of(
            "result", restaurants,
            "food", food,
            "review", reviews
        );
    }

    public Map<String, Object> leaveReview(LoginInfo loginInfo, LeaveReviewRequest request) {
        userService.checkUser(loginInfo.getUserId());

        Restaurants restaurants = restaurantsRepository.findById((long) request.getId())
            .orElseThrow(() -> new ClientException("식당 정보가 확인되지 않습니다."));

        Review review = Review.builder()
            .restaurantsId(request.getId())
            .star(request.getStar())
            .content(request.getContent())
            .userId(loginInfo.getUserId())
            .build();
        reviewRepository.save(review);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QReview r = QReview.review;

        double avg = queryFactory.select(r.star.avg())
            .from(r)
            .where(r.restaurantsId.eq(request.getId()).and(r.state.eq(0)))
            .fetchOne();

        restaurants.setStar(avg);
        restaurantsRepository.save(restaurants);

        return Map.of(
            "message", review
        );
    }

    public Map<String, Object> deleteReview(LoginInfo loginInfo, long id) {
        userService.checkUser(loginInfo.getUserId());

        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new ClientException("리뷰가 확인되지 않습니다."));
        review.setState(2);
        reviewRepository.save(review);

        Optional<Restaurants> restaurants = restaurantsRepository.findById((long) review.getRestaurantsId());
        if (restaurants.isPresent()) {
            JPAQueryFactory queryFactory = new JPAQueryFactory(em);
            QReview r = QReview.review;

            double avg = queryFactory.select(r.star.avg().nullif(0D))
                .from(r)
                .where(r.restaurantsId.eq(review.getRestaurantsId()).and(r.state.eq(0)))
                .fetchOne();

            restaurants.get().setStar(avg);
            restaurantsRepository.save(restaurants.get());
        }

        return Map.of(
            "message", review
        );
    }

}
