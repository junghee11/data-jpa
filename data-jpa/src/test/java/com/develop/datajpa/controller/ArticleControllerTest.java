package com.develop.datajpa.controller;

import com.develop.domain.dto.article.CommentDto;
import com.develop.domain.dto.user.LoginInfo;
import com.develop.domain.entity.article.Article;
import com.develop.domain.entity.article.ArticleType;
import com.develop.domain.entity.article.ArticleType.Category;
import com.develop.domain.entity.article.Comment;
import com.develop.domain.entity.article.CommentRecommend;
import com.develop.datajpa.request.article.AddCommentRequest;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.request.article.GetArticleListRequest;
import com.develop.datajpa.request.article.GetCommentListRequest;
import com.develop.datajpa.request.article.ModifyArticleRequest;
import com.develop.datajpa.request.article.ToggleCommentRequest;
import com.develop.datajpa.service.article.ArticleService;
import com.develop.datajpa.service.security.JwtProvider;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
            Article mockArticle = new Article("FOOD", title, content, userId);

            given(articleService.getArticleContent(1)).willReturn(
                Map.of(
                    "result", mockArticle
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
            given(articleService.getArticleContent(1)).willReturn(
                Map.of(
                    "result", new Article("FOOD", null, content, userId)
                ));

            String articleIdx = "idx";
            mockMvc.perform(get("/article/" + articleIdx)
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andDo(print());
        }

        @Test
        @DisplayName("모든 게시글 가져오기 - success")
        public void getArticleListSuccess() throws Exception {

            List<Article> mockArticles = List.of(new Article());
            Map<String, Object> mockResponse = Map.of(
                "pageCount", 1,
                "result", mockArticles
            );

            GetArticleListRequest request = new GetArticleListRequest();
            request.setCategory(Category.FOOD);

            when(articleService.getArticleList(any(GetArticleListRequest.class)))
                .thenReturn(mockResponse);

            mockMvc.perform(get("/article")
                    .param("category", Category.FOOD.name())
                    .param("page", "1")
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").isArray())
                .andDo(print());
        }

        @Test
        @DisplayName("모든 게시글 가져오기 - fail")
        public void getArticleListFail() throws Exception {
            mockMvc.perform(get("/article")
                    .param("category", Category.FOOD.name())
                    // request 페이지값 누락
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andDo(print());

            mockMvc.perform(get("/article")
                    .param("category", Category.FOOD.name())
                    .param("page", "0")
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("페이지값은 1보다 작을 수 없습니다"))
                .andDo(print());
        }
    }

    @DisplayName("게시글 생성하기")
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
            String requestBody = gson.toJson(request);

            given(articleService.createArticle(loginInfo, request)).willReturn(
                Map.of("message", "게시글 작성이 완료되었습니다")
            );

            mockMvc.perform(post("/article")
                    .header("Authorization", "Bearer " + jwtToken)
                    .content(requestBody)
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
            String requestBody = gson.toJson(request);

            given(articleService.createArticle(loginInfo, request)).willReturn(
                Map.of("message", "게시글 작성이 완료되었습니다")
            );

            mockMvc.perform(post("/article")
                    .header("Authorization", "Bearer " + null)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        }
    }

    @DisplayName("게시글 수정하기")
    @Nested
    class modifyArticleTest {
        @Test
        @DisplayName("success")
        public void modifyArticleSuccess() throws Exception {
            LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

            ModifyArticleRequest request = new ModifyArticleRequest();
            request.setId(1L);
            request.setTitle("수정할 제목입니다");
            request.setContent("수정할 내용입니다");

            Gson gson = new Gson();
            String requestBody = gson.toJson(request);

            given(articleService.modifyArticle(loginInfo, request)).willReturn(
                Map.of("message", "게시글이 수정되었습니다")
            );

            mockMvc.perform(patch("/article")
                    .header("Authorization", "Bearer " + jwtToken)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        }

        @Test
        @DisplayName("fail")
        public void modifyArticleFail() throws Exception {
            LoginInfo loginInfo = LoginInfo.builder().userId("test").build();

            ModifyArticleRequest request = new ModifyArticleRequest();
            request.setId(1L);
            request.setContent("수정할 내용입니다");

            Gson gson = new Gson();
            String requestBody = gson.toJson(request);

            given(articleService.modifyArticle(loginInfo, request)).willReturn(
                Map.of("message", "게시글이 수정되었습니다")
            );

            mockMvc.perform(patch("/article")
                    .header("Authorization", "Bearer " + jwtToken)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
        }
    }

    @DisplayName("게시글 삭제하기")
    @Nested
    class deleteArticleTest {
        @Test
        @DisplayName("success")
        public void deleteArticleSuccess() throws Exception {
            LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

            given(articleService.deleteArticle(loginInfo, 1L)).willReturn(
                Map.of(
                    "message", "게시글이 삭제되었습니다.")
            );

            mockMvc.perform(delete("/article/" + 1)
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());
        }

        @Test
        @DisplayName("fail")
        public void deleteArticleFail() throws Exception {
            mockMvc.perform(delete("/article/" + 1))
                .andExpect(status().isBadRequest())
                .andDo(print());
        }
    }

    @DisplayName("댓글 리스트 가져오기")
    @Nested
    class getCommentListTest {

        @Test
        @DisplayName("success")
        public void getCommentListSuccess() throws Exception {
            List<CommentDto> mockComments = List.of(new CommentDto());
            Map<String, Object> mockResponse = Map.of(
                "page", 1,
                "result", mockComments
            );

            GetCommentListRequest request = new GetCommentListRequest();
//            request.setId(3L);
            request.setPage(1);
            request.setCommentId(3L); // 대댓글 리스트 가져오기

//            when(articleService.getCommentList(jwtToken, request))
            when(articleService.getCommentList(null, request))
                .thenReturn(mockResponse);

            mockMvc.perform(get("/article/comments")
                    .param("id", "3")
                    .param("page", "1")
//                    .param("commentId", "3")
//                    .header("Authorization", "Bearer " + jwtToken))
                    .header("Authorization", "Bearer " + null))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        }

        @Test
        @DisplayName("fail")
        public void getCommentListFail() throws Exception {
            mockMvc.perform(get("/article/comments")
                    .param("id", "3")
                    .param("page", "0")
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("페이지값은 1보다 작을 수 없습니다"))
                .andDo(print());
        }
    }

    @DisplayName("댓글달기")
    @Nested
    class addCommentTest {
        @Test
        @DisplayName("success")
        public void addCommentSuccess() throws Exception {
            LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

            AddCommentRequest request = new AddCommentRequest();
            request.setArticleId(1L);
//            request.setCommentId(3L); // 대댓글
            request.setContent(content);

            Gson gson = new Gson();
            String requestBody = gson.toJson(request);

            given(articleService.addComment(loginInfo, request)).willReturn(
                Map.of("message", "댓글 작성이 완료되었습니다")
            );

            mockMvc.perform(post("/article/comment")
                    .header("Authorization", "Bearer " + jwtToken)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        }

        @Test
        @DisplayName("fail")
        public void addCommentFail() throws Exception {
            LoginInfo loginInfo = LoginInfo.builder().userId("test").build();

            AddCommentRequest request = new AddCommentRequest();
            request.setArticleId(1L);
            request.setContent("test");

            Gson gson = new Gson();
            String requestBody = gson.toJson(request);

            given(articleService.addComment(loginInfo, request)).willReturn(
                Map.of("message", "댓글은 최소 10자 이상, 최대 300자 이하로 작성해주세요")
            );

            mockMvc.perform(post("/article/comment")
                    .header("Authorization", "Bearer " + jwtToken)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
        }
    }

    @DisplayName("댓글 삭제하기")
    @Nested
    class deleteCommentTest {
        @Test
        @DisplayName("success")
        public void deleteCommentSuccess() throws Exception {
            LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

            given(articleService.deleteComment(loginInfo, 1L)).willReturn(
                Map.of(
                    "message", "댓글이 삭제되었습니다.")
            );

            mockMvc.perform(delete("/article/comment/" + 1)
                    .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());
        }

        @Test
        @DisplayName("fail")
        public void deleteCommentFail() throws Exception {
            given(articleService.deleteComment(null, 1L)).willReturn(
                Map.of(
                    "message", "Required header 'Authorization' is not present.")
            );

            mockMvc.perform(delete("/article/comment/" + 1))
                .andExpect(status().isBadRequest())
                .andDo(print());
        }
    }

    @DisplayName("댓글 추천하기")
    @Nested
    class toggleCommentTest {
        @Test
        @DisplayName("success")
        public void toggleCommentSuccess() throws Exception {
            LoginInfo loginInfo = LoginInfo.builder().userId(userId).build();

            ToggleCommentRequest request = new ToggleCommentRequest();
            request.setCommentId(1L);
            request.setRecommend(ArticleType.Recommend.UP);

            Gson gson = new Gson();
            String requestBody = gson.toJson(request);

            given(articleService.toggleComment(loginInfo, request)).willReturn(
                Map.of("comment", new Comment(),
                    "recommend", new CommentRecommend())
            );

            mockMvc.perform(patch("/article/comment")
                    .header("Authorization", "Bearer " + jwtToken)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        }

        @Test
        @DisplayName("fail")
        public void toggleCommentFail() throws Exception {
            LoginInfo loginInfo = LoginInfo.builder().userId("test").build();

            ToggleCommentRequest request = new ToggleCommentRequest();
            request.setCommentId(1L);
//            request.setRecommend(ArticleType.Recommend.UP);

            Gson gson = new Gson();
            String requestBody = gson.toJson(request);

            given(articleService.toggleComment(loginInfo, request)).willReturn(
                Map.of("message", "추천 혹은 비추천을 선택해주세요")
            );

            mockMvc.perform(patch("/article/comment")
                    .header("Authorization", "Bearer " + jwtToken)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
        }
    }

}
