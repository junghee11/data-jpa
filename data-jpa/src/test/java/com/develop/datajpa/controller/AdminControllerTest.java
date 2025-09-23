package com.develop.datajpa.controller;

import com.develop.datajpa.request.admin.*;
import com.develop.datajpa.service.admin.AdminService;
import com.develop.datajpa.service.security.JwtProvider;
import com.develop.domain.dto.user.LoginInfo;
import com.develop.domain.entity.baseball.MatchType;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AdminService adminService;

    private String jwtToken;
    private String userId;

    private String name;
    private int stadium;
    private boolean inside;
    private String address;
    private String phone;
    private String openingHours;
    private String webSite;
    private String imgUrl;

    @BeforeEach
    void setUp() {
        name = "고피자 한화이글스파크점";
        stadium = 9;
        inside = true;
        address = "대전 중구 대종로 373 한화이글스파크";
        phone = "010-XXXX-XXXX";
        openingHours = "17:00 - 22:00";
        webSite = "website1.com";
        imgUrl = "img1.jpg";

        userId = "member1";

        JwtProvider jwtProvider = new JwtProvider();
        jwtToken = jwtProvider.generateToken(userId);
    }

    @Test
    @DisplayName("식당 추가하기 - success")
    void registerRestaurantSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        RegisterRestaurantRequest request = new RegisterRestaurantRequest();
        request.setName(name);
        request.setStadium(stadium);
        request.setInside(inside);
        request.setAddress(address);
        request.setPhone(phone);
        request.setOpeningHours(openingHours);
        request.setWebSite(webSite);
        request.setImgUrl(imgUrl);

        given(adminService.registerRestaurant(loginInfo, request)).willReturn(
                Map.of("message", "식당이 등록되었습니다.")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/admin/baseball/restaurant")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("식당 추가하기 - fail")
    void registerRestaurantFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        RegisterRestaurantRequest request = new RegisterRestaurantRequest();
        request.setName(name);
//        request.setStadium(stadium);
        request.setInside(inside);
        request.setAddress(address);
        request.setPhone(phone);
        request.setOpeningHours(openingHours);
        request.setWebSite(webSite);
        request.setImgUrl(imgUrl);

        given(adminService.registerRestaurant(loginInfo, request)).willReturn(
                Map.of("message", "경기장을 선택해주세요")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/admin/baseball/restaurant")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("식당 정보 수정하기 - success")
    void updateRestaurantInfoSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        UpdateRestaurantInfoRequest request = new UpdateRestaurantInfoRequest();
        request.setId(1);
        request.setName("신규 매장 이름");
        request.setStadium(9);
        request.setInside(false);
        request.setAddress(address);
        request.setPhone("010-1234-1234");

        given(adminService.updateRestaurantInfo(loginInfo, request)).willReturn(
                Map.of("message", "식당 정보가 수정되었습니다.")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(patch("/admin/baseball/restaurant")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("식당 정보 수정하기 - fail")
    void updateRestaurantInfoFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        UpdateRestaurantInfoRequest request = new UpdateRestaurantInfoRequest();
        request.setId(1);
        request.setName("신규 매장 이름");
//        request.setStadium(9);
        request.setInside(false);
        request.setAddress(address);
        request.setPhone("010-1234-1234");

        given(adminService.updateRestaurantInfo(loginInfo, request)).willReturn(
                Map.of("message", "경기장을 선택해주세요")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(patch("/admin/baseball/restaurant")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("음식 메뉴 추가하기 - success")
    void addFoodMenuOnRestaurantSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        AddFoodMenuOnRestaurantRequest request = new AddFoodMenuOnRestaurantRequest();
        request.setId(1L);
        request.setName("떡볶이");
        request.setPrice(6000);
        request.setDesc("장인의 손맛이 들어간 우리가게 간판메뉴");
        request.setImgUrl("img.jpg");

        given(adminService.addFoodMenuOnRestaurant(loginInfo, request)).willReturn(
                Map.of("message", "메뉴가 등록되었습니다.")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/admin/baseball/restaurant/food")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("음식 메뉴 추가하기 - fail")
    void addFoodMenuOnRestaurantFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        AddFoodMenuOnRestaurantRequest request = new AddFoodMenuOnRestaurantRequest();
        request.setId(1L);
//        request.setName("떡볶이");
        request.setPrice(6000);
        request.setDesc("장인의 손맛이 들어간 우리가게 간판메뉴");
        request.setImgUrl("img.jpg");

        given(adminService.addFoodMenuOnRestaurant(loginInfo, request)).willReturn(
                Map.of("message", "메뉴 이름이 확인되지 않습니다.")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/admin/baseball/restaurant/food")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("음식 메뉴 삭제하기 - success")
    void deleteFoodInfoSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        Long foodId = 1L;
        given(adminService.deleteFoodInfo(loginInfo, foodId)).willReturn(
                Map.of("message", "해당 메뉴가 삭제되었습니다.")
        );

        mockMvc.perform(delete("/admin/baseball/restaurant/food/" + foodId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("음식 메뉴 삭제하기 - fail")
    void deleteFoodInfoFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        Long foodId = 1L;
        given(adminService.deleteFoodInfo(loginInfo, foodId)).willReturn(
                Map.of("message", "Required header 'Authorization' is not present.")
        );

        mockMvc.perform(delete("/admin/baseball/restaurant/food/" + foodId))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("신규 굿즈 추가하기 - success")
    void addTeamGoodsSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        AddTeamGoodsRequest request = new AddTeamGoodsRequest();
        request.setName("루피 머리띠");
        request.setTeam(MatchType.TeamCode.LG);
        request.setPrice(10000L);
        request.setDescription("나야.. 루피, 야구장의 귀요미는 나! 귀염뽀짝 루피인형 머리띠");
        request.setStock(10);
        request.setOnSale(true);
        request.setImgUrl("img1.jpg");
        request.setDiscountRate(0.1);
        request.setPointRate(0.03);

        given(adminService.addTeamGoods(loginInfo, request)).willReturn(
                Map.of("message", "상품이 등록되었습니다.")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/admin/baseball/shop/goods")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("신규 굿즈 추가하기 - fail")
    void addTeamGoodsFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        AddTeamGoodsRequest request = new AddTeamGoodsRequest();
        request.setName("루피 머리띠");
        request.setTeam(MatchType.TeamCode.LG);
        request.setPrice(10000L);
        request.setDescription("나야.. 루피, 야구장의 귀요미는 나! 귀염뽀짝 루피인형 머리띠");
        request.setStock(10);
        request.setOnSale(true);
        request.setImgUrl("img1.jpg");
        request.setDiscountRate(10D);
        request.setPointRate(0.03);

        given(adminService.addTeamGoods(loginInfo, request)).willReturn(
                Map.of("message", "할인율은 1 이하여야 합니다.")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/admin/baseball/shop/goods")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("굿즈 삭제하기 - success")
    void deleteTeamGoodsSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        String foodCode = "GOODS123";
        given(adminService.deleteTeamGoods(loginInfo, foodCode)).willReturn(
                Map.of("message", "상품이 삭제되었습니다.")
        );

        mockMvc.perform(delete("/admin/baseball/shop/goods/" + foodCode)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("굿즈 삭제하기 - fail")
    void deleteTeamGoodsFail() throws Exception {
        String goodsCode = "TEST123123";

        mockMvc.perform(delete("/admin/baseball/shop/goods/" + goodsCode)
                        .header("Authorization", "invalid-user-token"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("굿즈 정보 수정하기 - success")
    void updateTeamGoodsInfoSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        UpdateTeamGoodsInfoRequest request = new UpdateTeamGoodsInfoRequest();
        request.setId("TEST123123");
        request.setName("루피 머리띠");
        request.setPrice(10000L);
        request.setDescription("나야.. 루피, 야구장의 귀요미는 나! 귀염뽀짝 루피인형 머리띠");
        request.setStock(10);
        request.setImgUrl("img1.jpg");
        request.setDiscountRate(0.1);
        request.setPointRate(0.03);

        given(adminService.updateTeamGoodsInfo(loginInfo, request)).willReturn(
                Map.of("message", "굿즈 정보가 수정되었습니다.")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(patch("/admin/baseball/shop/goods")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("굿즈 정보 수정하기 - fail")
    void updateTeamGoodsInfoFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        UpdateTeamGoodsInfoRequest request = new UpdateTeamGoodsInfoRequest();
        request.setId("TEST123123");
        request.setName("루피 머리띠");
//        request.setPrice(10000L);
        request.setDescription("나야.. 루피, 야구장의 귀요미는 나! 귀염뽀짝 루피인형 머리띠");
        request.setStock(10);
        request.setImgUrl("img1.jpg");
        request.setDiscountRate(0.1);
        request.setPointRate(0.03);

        given(adminService.updateTeamGoodsInfo(loginInfo, request)).willReturn(
                Map.of("message", "가격 정보가 확인되지 않습니다.")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(patch("/admin/baseball/shop/goods")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 차단하기 - success")
    void blockArticleSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        Long articleId = 1L;
        given(adminService.blockArticle(loginInfo, articleId)).willReturn(
                Map.of("message", "해당 게시글이 차단 처리되었습니다.")
        );

        mockMvc.perform(patch("/admin/article/" + articleId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 차단하기 - fail")
    void blockArticleFail() throws Exception {
        String articleId = "bad ID type";

        mockMvc.perform(patch("/admin/article/" + articleId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 차단하기 - success")
    void blockCommentSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        Long commentId = 1L;
        given(adminService.blockComment(loginInfo, commentId)).willReturn(
                Map.of("message", "해당 댓글이 차단 처리되었습니다.")
        );

        mockMvc.perform(patch("/admin/article/comment/" + commentId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 차단하기 - fail")
    void blockCommentFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId("invalid-user-id").build();

        Long commentId = 1L;
        given(adminService.blockComment(loginInfo, commentId)).willReturn(
                Map.of("message", "로그인 정보가 확인되지 않습니다.")
        );

        mockMvc.perform(patch("/admin/article/comment/" + commentId)
                        .header("Authorization", "invalid-user-token"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

}
