package com.develop.datajpa.controller;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.Article;
import com.develop.datajpa.entity.ArticleType;
import com.develop.datajpa.entity.ArticleType.Category;
import com.develop.datajpa.repository.ArticleRepository;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.request.article.GetArticleListRequest;
import com.develop.datajpa.service.article.ArticleService;
import com.develop.datajpa.service.security.JwtProvider;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    private String title;
    private String content;
    private String userId;

    @BeforeEach
    void setUp() {
        title = "안녕하세요";
        content = "오늘은 떡볶이 맛집에 다녀왔어요";
        userId = "member1";

        JwtProvider jwtProvider = new JwtProvider();
        jwtToken = jwtProvider.generateToken(userId);
    }

    @AfterAll
    static void close() {
        System.out.println("test 종료");
    }

    @DisplayName("게시글 가져오기")
    @Nested
    class getArticleTest {
        @Test
        @DisplayName("특정 게시글 가져오기 - success")
        public void getArticleSuccess() throws Exception {
            given(articleService.getArticle(1)).willReturn(
                Map.of(
                    "result", new Article("FOOD", title, content, userId)
                ));

            Long articleIdx = 1L;
            mockMvc.perform(get("/article/" + articleIdx)
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.title").value(title))
                .andDo(print());
        }

        @Test
        @DisplayName("특정 게시글 가져오기 - fail")
        public void getArticleFail() throws Exception {
            given(articleService.getArticle(1)).willReturn(
                Map.of(
                    "result", new Article("FOOD", title, content, userId)
                ));

            Long articleIdx = 1L;
            mockMvc.perform(get("/article/" + articleIdx)
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.title").value("실패한 제목입니다"))
                .andDo(print());
        }

        @Test
        @DisplayName("모든 게시글 가져오기 - success")
        public void getArticleListSuccess() throws Exception {

            GetArticleListRequest request = new GetArticleListRequest();
            request.setCategory(Category.FOOD);

            when(articleService.getArticleList(request))
                .thenReturn(Map.of(
                    "pageCount", Number.class,
                    "result", List.of(Article.class)
                ));

            mockMvc.perform(get("/article?category=" + Category.FOOD.name() + "&page=" + 1)
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.result", is(notNullValue())))
                .andDo(print());
        }

        @Test
        @DisplayName("모든 게시글 가져오기 - fail")
        public void getArticleListFail() throws Exception {

            mockMvc.perform(get("/article?category=" + Category.FOOD.name())
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());
        }
    }

    @DisplayName("게시글 작성하기")
    @Nested
    class createArticleTest {
        @Test
        @DisplayName("success")
        public void createArticleSuccess() throws Exception {
            LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

            CreateArticleRequest request = new CreateArticleRequest();
            request.setCategory(Category.FOOD);
            request.setTitle(title);
            request.setContent(content);

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

        @Test
        @DisplayName("fail")
        public void createArticleFail() throws Exception {
            LoginInfo loginInfo = LoginInfo.builder().userId("test").build();

            CreateArticleRequest request = new CreateArticleRequest();
            request.setCategory(Category.FOOD);
            request.setTitle(title);
            request.setContent(content);

            Gson gson = new Gson();
            String content = gson.toJson(request);

            given(articleService.createArticle(loginInfo, request)).willReturn(
                Map.of(
                    "message", "게시글 작성이 완료되었습니다")
            );

            mockMvc.perform(post("/article")
                    .header("Authorization", "Bearer " + null)
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        }
    }




}
