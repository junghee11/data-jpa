package com.develop.datajpa.controller;

import com.develop.datajpa.request.shop.AddCartRequest;
import com.develop.datajpa.request.shop.PurchaseGoodsRequest;
import com.develop.datajpa.response.kakao.KakaoPayReadyDto;
import com.develop.datajpa.service.baseball.ShopService;
import com.develop.datajpa.service.security.JwtProvider;
import com.develop.domain.dto.user.LoginInfo;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ShopService shopService;

    private String jwtToken;
    private String userId;

    private String goodsId;
    private int count;

    @BeforeEach
    void setUp() {
        goodsId = "TEST_GOODS_CODE";
        count = 9;

        userId = "member1";

        JwtProvider jwtProvider = new JwtProvider();
        jwtToken = jwtProvider.generateToken(userId);
    }

    @Test
    @DisplayName("장바구니 담기 - success")
    void addCartSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        AddCartRequest request = new AddCartRequest();
        request.setId(goodsId);
        request.setCount(count);

        given(shopService.addCart(loginInfo, request)).willReturn(
                Map.of("message", "장바구니에 추가했습니다.")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/shop/goods/cart")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 담기 - fail")
    void addCartFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        AddCartRequest request = new AddCartRequest();
        request.setId(goodsId);
        request.setCount(0);

        given(shopService.addCart(loginInfo, request)).willReturn(
                Map.of("message", "갯수가 1 이상이여야 합니다")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/shop/goods/cart")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니에서 삭제 - success")
    void removeCartSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        given(shopService.removeCart(loginInfo, goodsId)).willReturn(
                Map.of("message", "장바구니에서 삭제되었습니다.")
        );

        mockMvc.perform(delete("/shop/goods/cart/" + goodsId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니에서 삭제 - fail")
    void removeCartFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        given(shopService.removeCart(loginInfo, goodsId)).willReturn(
                Map.of("message", "로그인 정보가 확인되지 않습니다.")
        );

        mockMvc.perform(delete("/shop/goods/cart/" + goodsId)
                        .header("Authorization", "invalid-user-token"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 비우기 - success")
    void clearCartSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        given(shopService.clearCart(loginInfo)).willReturn(
                Map.of("message", "장바구니에서 삭제되었습니다.")
        );

        mockMvc.perform(delete("/shop/goods/cart")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 비우기 - fail")
    void clearCartFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        given(shopService.clearCart(loginInfo)).willReturn(
                Map.of("message", "로그인 정보가 확인되지 않습니다.")
        );

        mockMvc.perform(delete("/shop/goods/cart")
                        .header("Authorization", "invalid-user-token"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("물건 구입하기 - success")
    void purchaseGoodsSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        PurchaseGoodsRequest request = new PurchaseGoodsRequest();
        request.setId(goodsId);
        request.setCount(count);

        given(shopService.purchaseGoods(loginInfo, request)).willReturn(
                Map.of("result", new KakaoPayReadyDto())

        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/shop/goods")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("물건 구입하기 - fail")
    void purchaseGoodsFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        PurchaseGoodsRequest request = new PurchaseGoodsRequest();
//        request.setId(goodsId);
        request.setCount(count);

        given(shopService.purchaseGoods(loginInfo, request)).willReturn(
                Map.of("message", "상품정보가 확인되지 않습니다")
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(request);

        mockMvc.perform(post("/shop/goods")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 물건 구입하기 - success")
    void orderShoppingCartSuccess() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        given(shopService.orderShoppingCart(loginInfo)).willReturn(
                Map.of("result", "결제가 완료되었습니다")

        );

        Gson gson = new Gson();

        mockMvc.perform(post("/shop/goods/order-cart")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 물건 구입하기 - fail")
    void orderShoppingCartFail() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

        given(shopService.orderShoppingCart(loginInfo)).willReturn(
                Map.of("message", "로그인 정보가 확인되지 않습니다.")
        );

        mockMvc.perform(post("/shop/goods/order-cart")
                        .header("Authorization", "invalid-user-token"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

}
