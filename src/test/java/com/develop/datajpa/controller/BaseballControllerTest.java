package com.develop.datajpa.controller;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.baseball.Review;
import com.develop.datajpa.request.baseball.LeaveReviewRequest;
import com.develop.datajpa.service.baseball.BaseballService;
import com.develop.datajpa.service.security.JwtProvider;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BaseballControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BaseballService baseballService;

    private String jwtToken;
    private String userId;
    private long restaurantsId;
    private int star;
    private String content;

    @BeforeEach
    void setUp() {
        restaurantsId = 1;
        star = 5;
        content = "좋아하는 맛집이라 자주 들립니다ㅎㅎ";

        userId = "member1";

        JwtProvider jwtProvider = new JwtProvider();
        jwtToken = jwtProvider.generateToken(userId);
    }

    @Test
    @DisplayName("식당 리뷰 남기기 - success")
    void leaveReviewSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        LeaveReviewRequest request = new LeaveReviewRequest();
        request.setId(restaurantsId);
        request.setStar(star);
        request.setContent(content);

        given(baseballService.leaveReview(loginInfo, request)).willReturn(
            Map.of("review", Review.class)
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/baseball/restaurant/review")
                .header("Authorization", "Bearer " + jwtToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("식당 리뷰 남기기 - fail")
    void leaveReviewFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        LeaveReviewRequest request = new LeaveReviewRequest();
        request.setId(restaurantsId);
        request.setStar(6);
        request.setContent(content);

        given(baseballService.leaveReview(loginInfo, request)).willReturn(
            Map.of("message", "평점은 최대 5점을 초과할 수 없습니다")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/baseball/restaurant/review")
                .header("Authorization", "Bearer " + jwtToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

}
