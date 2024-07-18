package com.develop.datajpa.service;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.ArticleType.Category;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.service.article.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    ArticleService articleService;

    @Nested
    @DisplayName("게시글 생성")
    @Test
    public void createArticle() throws Exception {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setCategory(Category.FOOD);
        request.setTitle("안녕하세요");
        request.setContent("오늘은 떡볶이 맛집에 다녀왔어요");

        LoginInfo loginInfo = LoginInfo.builder().userId("zena").build();

        articleService.createArticle(loginInfo, request);
    }

}
