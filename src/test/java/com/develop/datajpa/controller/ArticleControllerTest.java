package com.develop.datajpa.controller;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.Article;
import com.develop.datajpa.entity.ArticleType.Category;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.service.article.ArticleService;
import com.develop.datajpa.service.security.JwtProvider;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ArticleService articleService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        JwtProvider jwtProvider = new JwtProvider();
        jwtToken = jwtProvider.generateToken("member1");
    }

    @AfterAll
    static void close() {
        System.out.println("article test is done..");
    }

    @Test
    @DisplayName("article - 특정 게시글 가져오기 test")
    public void getArticle() throws Exception {
        given(articleService.getArticle(1)).willReturn(
            Map.of(
                "result", new Article("FOOD", "맛집 추천드려요", "", "zena")
            ));

        long articleIdx = 1L;
        mockMvc.perform(get("/article/" + articleIdx)
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.title").value("맛집 추천드려요"))
            .andDo(print());
    }

    @Test
    @DisplayName("article - 게시글 작성하기 test")
    public void createArticle() throws Exception {
        LoginInfo loginInfo = LoginInfo.builder().userId("member1").build();

        CreateArticleRequest request = new CreateArticleRequest();
        request.setCategory(Category.FOOD);
        request.setTitle("안녕하세요");
        request.setContent("오늘은 떡볶이 맛집에 다녀왔어요");

        Gson gson = new Gson();
        String content = gson.toJson(request);

        given(articleService.createArticle(loginInfo, request)).willReturn(
            Map.of(
                "message", "게시글 작성이 완료되었습니다")
            );

        mockMvc.perform(post("/article")
                .header("Authorization", "Bearer " + jwtToken)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());
    }

}
