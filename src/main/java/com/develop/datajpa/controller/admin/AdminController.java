package com.develop.datajpa.controller.admin;


import com.develop.datajpa.request.admin.AddFoodMenuOnRestaurantRequest;
import com.develop.datajpa.request.admin.AddTeamGoodsRequest;
import com.develop.datajpa.request.admin.RecordMatchResultRequest;
import com.develop.datajpa.request.admin.RegisterRestaurantRequest;
import com.develop.datajpa.request.admin.UpdateFoodInfoRequest;
import com.develop.datajpa.request.admin.UpdateRestaurantInfoRequest;
import com.develop.datajpa.request.admin.UpdateTeamGoodsInfoRequest;
import com.develop.datajpa.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.develop.datajpa.service.security.JwtProvider.resolveToken;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/baseball/result")
    public Map<String, Object> recordMatchResult(@RequestHeader(value = "Authorization") String token,
                                                 @Valid RecordMatchResultRequest request) {
        return adminService.recordMatchResult(resolveToken(token), request);
    }

    @PatchMapping("/baseball/{id}")
    public Map<String, Object> cancelMatch(@RequestHeader(value = "Authorization") String token,
                                           @PathVariable("id") long id) {
        return adminService.cancelMatch(resolveToken(token), id);
    }

    @PostMapping("/baseball/restaurant")
    public Map<String, Object> registerRestaurant(@RequestHeader(value = "Authorization") String token,
                                                  @Valid @RequestBody RegisterRestaurantRequest request) {
        return adminService.registerRestaurant(resolveToken(token), request);
    }

    @PatchMapping("/baseball/restaurant")
    public Map<String, Object> updateRestaurantInfo(@RequestHeader(value = "Authorization") String token,
                                                    @Valid @RequestBody UpdateRestaurantInfoRequest request) {
        return adminService.updateRestaurantInfo(resolveToken(token), request);
    }

    @PostMapping("/baseball/restaurant/food")
    public Map<String, Object> addFoodMenuOnRestaurant(@RequestHeader(value = "Authorization") String token,
                                                       @Valid @RequestBody AddFoodMenuOnRestaurantRequest request) {
        return adminService.addFoodMenuOnRestaurant(resolveToken(token), request);
    }

    @PatchMapping("/baseball/restaurant/food")
    public Map<String, Object> updateFoodInfo(@RequestHeader(value = "Authorization") String token,
                                              @Valid @RequestBody UpdateFoodInfoRequest request) {
        return adminService.updateFoodInfo(resolveToken(token), request);
    }

    @DeleteMapping("/baseball/restaurant/food/{id}")
    public Map<String, Object> deleteFoodInfo(@RequestHeader(value = "Authorization") String token,
                                              @PathVariable("id") long id) {
        return adminService.deleteFoodInfo(resolveToken(token), id);
    }

    @PostMapping("/baseball/shop/goods")
    public Map<String, Object> addTeamGoods(@RequestHeader(value = "Authorization") String token,
                                            @Valid @RequestBody AddTeamGoodsRequest request) {
        return adminService.addTeamGoods(resolveToken(token), request);
    }

    @DeleteMapping("/baseball/shop/goods/{id}")
    public Map<String, Object> deleteTeamGoods(@RequestHeader(value = "Authorization") String token,
                                               @PathVariable("id") String id) {
        return adminService.deleteTeamGoods(resolveToken(token), id);
    }

    @PatchMapping("/baseball/shop/goods/{id}")
    public Map<String, Object> updateTeamGoodsInfo(@RequestHeader(value = "Authorization") String token,
                                                   @Valid @RequestBody UpdateTeamGoodsInfoRequest request) {
        return adminService.updateTeamGoodsInfo(resolveToken(token), request);
    }

}
