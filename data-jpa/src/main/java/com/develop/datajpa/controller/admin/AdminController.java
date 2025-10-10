package com.develop.datajpa.controller.admin;


import com.develop.core.security.jwt.CustomUserDetails;
import com.develop.datajpa.request.admin.*;
import com.develop.datajpa.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/baseball/result")
    public Map<String, Object> recordMatchResult(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @Valid RecordMatchResultRequest request) {
        return adminService.recordMatchResult(userDetails.getLoginInfo(), request);
    }

    @PatchMapping("/baseball/{id}")
    public Map<String, Object> cancelMatch(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @PathVariable("id") long id) {
        return adminService.cancelMatch(userDetails.getLoginInfo(), id);
    }

    @PostMapping("/baseball/restaurant")
    public Map<String, Object> registerRestaurant(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @Valid @RequestBody RegisterRestaurantRequest request) {
        return adminService.registerRestaurant(userDetails.getLoginInfo(), request);
    }

    @PatchMapping("/baseball/restaurant")
    public Map<String, Object> updateRestaurantInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @Valid @RequestBody UpdateRestaurantInfoRequest request) {
        return adminService.updateRestaurantInfo(userDetails.getLoginInfo(), request);
    }

    @PostMapping("/baseball/restaurant/food")
    public Map<String, Object> addFoodMenuOnRestaurant(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                       @Valid @RequestBody AddFoodMenuOnRestaurantRequest request) {
        return adminService.addFoodMenuOnRestaurant(userDetails.getLoginInfo(), request);
    }

    @PatchMapping("/baseball/restaurant/food")
    public Map<String, Object> updateFoodInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @Valid @RequestBody UpdateFoodInfoRequest request) {
        return adminService.updateFoodInfo(userDetails.getLoginInfo(), request);
    }

    @DeleteMapping("/baseball/restaurant/food/{id}")
    public Map<String, Object> deleteFoodInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable("id") long id) {
        return adminService.deleteFoodInfo(userDetails.getLoginInfo(), id);
    }

    @PostMapping("/baseball/shop/goods")
    public Map<String, Object> addTeamGoods(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @Valid @RequestBody AddTeamGoodsRequest request) {
        return adminService.addTeamGoods(userDetails.getLoginInfo(), request);
    }

    @DeleteMapping("/baseball/shop/goods/{id}")
    public Map<String, Object> deleteTeamGoods(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @PathVariable("id") String id) {
        return adminService.deleteTeamGoods(userDetails.getLoginInfo(), id);
    }

    @PatchMapping("/baseball/shop/goods")
    public Map<String, Object> updateTeamGoodsInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @Valid @RequestBody UpdateTeamGoodsInfoRequest request) {
        return adminService.updateTeamGoodsInfo(userDetails.getLoginInfo(), request);
    }

    @PatchMapping("/article/{id}")
    public Map<String, Object> blockArticle(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable("id") long id) {
        return adminService.blockArticle(userDetails.getLoginInfo(), id);
    }

    @PatchMapping("/article/comment/{id}")
    public Map<String, Object> blockComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable("id") long id) {
        return adminService.blockComment(userDetails.getLoginInfo(), id);
    }

}
