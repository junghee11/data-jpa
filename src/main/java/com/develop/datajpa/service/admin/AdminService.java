package com.develop.datajpa.service.admin;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.baseball.Food;
import com.develop.datajpa.entity.baseball.MatchSchedule;
import com.develop.datajpa.entity.baseball.MatchType.MatchResult;
import com.develop.datajpa.entity.baseball.Restaurants;
import com.develop.datajpa.repository.FoodRepository;
import com.develop.datajpa.repository.MatchScheduleRepository;
import com.develop.datajpa.repository.RestaurantsRepository;
import com.develop.datajpa.request.admin.AddFoodMenuOnRestaurantRequest;
import com.develop.datajpa.request.admin.RecordMatchResultRequest;
import com.develop.datajpa.request.admin.RegisterRestaurantRequest;
import com.develop.datajpa.request.admin.UpdateFoodInfoRequest;
import com.develop.datajpa.request.admin.UpdateRestaurantInfoRequest;
import com.develop.datajpa.response.ClientException;
import com.develop.datajpa.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final MatchScheduleRepository matchScheduleRepository;
    private final RestaurantsRepository restaurantsRepository;
    private final FoodRepository foodRepository;

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

}
