package com.develop.datajpa.service;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.Article;
import com.develop.datajpa.entity.ArticleType;
import com.develop.datajpa.entity.ArticleType.Category;
import com.develop.datajpa.repository.ArticleRepository;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.service.article.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    ArticleService articleService;
//    ArticleRepository articleRepository;

    // https://velog.io/@nimoh/Spring-TDDTest-Driven-Development-%EC%8B%9C%EB%8F%84%ED%95%B4%EB%B3%B4%EA%B8%B0

    @Nested
    @DisplayName("게시글 생성")
    @Test
    public void createArticle() throws Exception {

        String title = "안녕하세요";
        String content = "오늘은 떡볶이 맛집에 다녀왔어요";

//        Article article = Article.builder()
//            .category(ArticleType.Category.FOOD.name())
//            .userId("zena")
//            .title(title)
//            .content(content)
//            .build();
//
//        Article newArticle = articleRepository.save(article);
//
//        assertThat(newArticle).isNotNull();
//        assertThat(newArticle).isInstanceOf(Article.class);
//
//        Optional<Article> findArticle = articleRepository.findById(newArticle.getIdx());
//
//        assertThat(findArticle.get()).isNotNull();
//        assertThat(findArticle.get()).isEqualTo(newArticle);

        CreateArticleRequest request = new CreateArticleRequest();
        request.setCategory(Category.FOOD);
        request.setTitle(title);
        request.setContent(content);

        LoginInfo loginInfo = LoginInfo.builder().userId("zena").build();

        Map<String, Object> result = articleService.createArticle(loginInfo, request);
        assertNotNull(result);
        assertEquals("게시글 작성이 완료되었습니다.", result.get("message"));

    }

}
