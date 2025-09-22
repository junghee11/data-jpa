package com.develop.datajpa.service.admin;

import com.develop.domain.dto.user.LoginInfo;
import com.develop.domain.entity.article.Article;
import com.develop.domain.entity.article.ArticleType.ArticleState;
import com.develop.domain.entity.article.ArticleType.CommentState;
import com.develop.domain.entity.article.Comment;
import com.develop.domain.entity.baseball.Food;
import com.develop.domain.entity.baseball.MatchSchedule;
import com.develop.domain.entity.baseball.MatchType.MatchResult;
import com.develop.domain.entity.baseball.Restaurants;
import com.develop.domain.entity.shop.Goods;
import com.develop.domain.entity.shop.GoodsType.State;
import com.develop.domain.repository.article.ArticleRepository;
import com.develop.domain.repository.article.CommentRepository;
import com.develop.domain.repository.baseball.FoodRepository;
import com.develop.domain.repository.baseball.MatchScheduleRepository;
import com.develop.domain.repository.baseball.RestaurantsRepository;
import com.develop.domain.repository.shop.GoodsRepository;
import com.develop.datajpa.request.admin.AddFoodMenuOnRestaurantRequest;
import com.develop.datajpa.request.admin.AddTeamGoodsRequest;
import com.develop.datajpa.request.admin.RecordMatchResultRequest;
import com.develop.datajpa.request.admin.RegisterRestaurantRequest;
import com.develop.datajpa.request.admin.UpdateFoodInfoRequest;
import com.develop.datajpa.request.admin.UpdateRestaurantInfoRequest;
import com.develop.datajpa.request.admin.UpdateTeamGoodsInfoRequest;
import com.develop.core.exception.ClientException;
import com.develop.datajpa.service.article.ArticleService;
import com.develop.datajpa.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final ArticleService articleService;
    private final MatchScheduleRepository matchScheduleRepository;
    private final RestaurantsRepository restaurantsRepository;
    private final FoodRepository foodRepository;
    private final GoodsRepository goodsRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public Map<String, Object> recordMatchResult(LoginInfo loginInfo, RecordMatchResultRequest request) {
        userService.checkAdmin(loginInfo.getUserId());

        MatchSchedule match = matchScheduleRepository.findById(request.getIdx())
            .orElseThrow(() -> new ClientException("경기정보가 확인되지 않습니다."));

        LocalDateTime matchTime = LocalDateTime.of(match.getMatchDate(), match.getMatchTime());
        if (LocalDateTime.now().isBefore(matchTime)) {
            throw new ClientException("경기가 아직 시작되지 않았습니다.");
        }

        match.setHomeScore(request.getHome());
        match.setAwayScore(request.getAway());
        match.setMatchResult(MatchResult.getResult(request.getHome(), request.getAway()));

        matchScheduleRepository.save(match);

        return Map.of(
            "message", "경기결과가 입력되었습니다."
        );
    }

    public Map<String, Object> cancelMatch(LoginInfo loginInfo, long id) {
        userService.checkAdmin(loginInfo.getUserId());

        MatchSchedule match = matchScheduleRepository.findByIdxAndMatchResult(id, MatchResult.INITIAL)
            .orElseThrow(() -> new ClientException("이미 처리되었거나 존재하지 않은 경기번호 입니다."));

        match.setMatchResult(MatchResult.CANCELED);
        matchScheduleRepository.save(match);

        return Map.of(
            "message", "경기결과가 입력되었습니다."
        );
    }

    public Map<String, Object> registerRestaurant(LoginInfo loginInfo, RegisterRestaurantRequest request) {
        userService.checkAdmin(loginInfo.getUserId());

        Restaurants newRestaurant = Restaurants.builder()
            .name(request.getName())
            .stadium(request.getStadium())
            .inside(request.getInside())
            .address(request.getAddress())
            .phone(request.getPhone())
            .openingHours(request.getOpeningHours())
            .website(request.getWebSite())
            .imgUrl(request.getImgUrl())
            .build();
        restaurantsRepository.save(newRestaurant);

        return Map.of(
            "message", "식당이 등록되었습니다."
        );
    }

    public Map<String, Object> updateRestaurantInfo(LoginInfo loginInfo, UpdateRestaurantInfoRequest request) {
        userService.checkAdmin(loginInfo.getUserId());

        Restaurants restaurant = restaurantsRepository.findById(request.getId())
            .orElseThrow(() -> new ClientException("식당 정보가 확인되지 않습니다."));

        restaurant.setName(request.getName());
        restaurant.setInside(request.getInside());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        restaurant.setOpeningHours(request.getOpeningHours());
        restaurant.setWebsite(request.getWebSite());
        restaurant.setImgUrl(request.getImgUrl());
        restaurant.setUpdatedAt(LocalDateTime.now());

        restaurantsRepository.save(restaurant);

        return Map.of(
            "message", "식당 정보가 수정되었습니다."
        );
    }

    public Map<String, Object> addFoodMenuOnRestaurant(LoginInfo loginInfo, AddFoodMenuOnRestaurantRequest request) {
        userService.checkAdmin(loginInfo.getUserId());

        restaurantsRepository.findById(request.getId())
            .orElseThrow(() -> new ClientException("식당 정보가 확인되지 않습니다."));

        Food food = Food.builder()
            .restaurantsId(request.getId())
            .name(request.getName())
            .price(request.getPrice())
            .description(request.getDesc())
            .imgUrl(request.getImgUrl())
            .build();
        foodRepository.save(food);

        return Map.of(
            "message", "메뉴가 등록되었습니다."
        );
    }

    public Map<String, Object> updateFoodInfo(LoginInfo loginInfo, UpdateFoodInfoRequest request) {
        userService.checkAdmin(loginInfo.getUserId());

        Food food = foodRepository.findById(request.getId())
            .orElseThrow(() -> new ClientException("메뉴가 확인되지 않습니다."));

        food.setName(request.getName());
        food.setPrice(request.getPrice());
        food.setDescription(request.getDesc());
        food.setImgUrl(request.getImgUrl());
        food.setUpdatedAt(LocalDateTime.now());
        foodRepository.save(food);

        return Map.of(
            "message", "메뉴 정보가 수정되었습니다."
        );
    }

    public Map<String, Object> deleteFoodInfo(LoginInfo loginInfo, long id) {
        userService.checkAdmin(loginInfo.getUserId());

        Food food = foodRepository.findById(id)
            .orElseThrow(() -> new ClientException("메뉴가 확인되지 않습니다."));

        foodRepository.delete(food);

        return Map.of(
            "message", "해당 메뉴가 삭제되었습니다."
        );
    }

    private String getRandomCode(int length) {
        Random random = new Random();

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (random.nextBoolean())
                buffer.append((char) (random.nextInt(26) + 65));   // 0~25(26개) + 65
            else
                buffer.append(random.nextInt(10));
        }
        return buffer.toString();
    }

    public Map<String, Object> addTeamGoods(LoginInfo loginInfo, AddTeamGoodsRequest request) {
        userService.checkAdmin(loginInfo.getUserId());

        String currentTime = getRandomCode(10);

        Goods goods = Goods.builder()
            .goodsCode(request.getTeam().get().substring(0, 2) + currentTime)
            .name(request.getName())
            .team(request.getTeam().get())
            .description(request.getDescription())
            .price(request.getPrice())
            .stock(request.getStock())
            .onSale(request.getOnSale())
            .imgUrl(request.getImgUrl())
            .discountRate(request.getDiscountRate())
            .pointRate(request.getPointRate())
            .build();
        goodsRepository.save(goods);

        return Map.of(
            "message", "상품이 등록되었습니다."
        );
    }

    public Map<String, Object> deleteTeamGoods(LoginInfo loginInfo, String id) {
        userService.checkAdmin(loginInfo.getUserId());

        Goods goods = goodsRepository.findByGoodsCode(id)
            .orElseThrow(() -> new ClientException("상품 정보가 확인되지 않습니다."));

        if (goods.getGoodsState() != State.NORMAL) {
            throw new ClientException("이미 삭제되었거나 판매 승인 거부 상품입니다. 관리자에게 문의해주세요.");
        }
        goods.setGoodsState(State.REMOVED);
        goodsRepository.save(goods);

        return Map.of(
            "message", "상품이 삭제되었습니다."
        );
    }

    public Map<String, Object> updateTeamGoodsInfo(LoginInfo loginInfo, UpdateTeamGoodsInfoRequest request) {
        userService.checkAdmin(loginInfo.getUserId());

        Goods goods = goodsRepository.findByGoodsCode(request.getId())
            .orElseThrow(() -> new ClientException("상품 정보가 확인되지 않습니다."));

        if (goods.getGoodsState() != State.NORMAL) {
            throw new ClientException("판매 승인 거부 상품입니다. 관리자에게 문의해주세요.");
        }

        goods.setName(request.getName());
        goods.setPrice(request.getPrice());
        goods.setDescription(request.getDescription());
        goods.setStock(request.getStock());
        goods.setImgUrl(request.getImgUrl());
        goods.setDiscountRate(request.getDiscountRate());
        goods.setPointRate(request.getPointRate());

        goodsRepository.save(goods);

        return Map.of(
            "message", "상품정보가 수정되었습니다."
        );
    }

    public Map<String, Object> blockArticle(LoginInfo loginInfo, Long id) {
        userService.checkAdmin(loginInfo.getUserId());

        Article article = articleService.getArticle(id);

        article.setState(ArticleState.BLOCKED.ordinal());
        articleRepository.save(article);

        return Map.of(
            "message", "해당 게시글이 차단 처리되었습니다."
        );
    }

    public Map<String, Object> blockComment(LoginInfo loginInfo, Long id) {
        userService.checkAdmin(loginInfo.getUserId());

        Comment comment = articleService.getComment(id);

        comment.setState(CommentState.BLOCKED.ordinal());
        commentRepository.save(comment);

        return Map.of(
            "message", "해당 댓글이 차단 처리되었습니다."
        );
    }

}
