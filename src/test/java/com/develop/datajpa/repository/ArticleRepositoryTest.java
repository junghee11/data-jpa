package com.develop.datajpa.repository;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.Article;
import com.develop.datajpa.entity.ArticleType;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.service.article.ArticleService;
import com.develop.datajpa.service.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;

    private String title;
    private String content;
    private String userId;

    @BeforeEach
    void setUp() {
        title = "안녕하세요";
        content = "오늘은 떡볶이 맛집에 다녀왔어요";
        userId = "member1";
    }

    @DisplayName("게시글 생성")
    @Test
    public void createArticleSuccess() throws Exception {
        Article article = Article.builder()
            .category(ArticleType.Category.FOOD.name())
            .userId(userId)
            .title(title)
            .content(content)
            .build();

        Article newArticle = articleRepository.save(article);

        assertThat(newArticle).isNotNull();
        assertThat(newArticle).isInstanceOf(Article.class);

        Optional<Article> findArticle = articleRepository.findById(newArticle.getIdx());

        assertThat(findArticle.get()).isNotNull();
        assertThat(findArticle.get()).isEqualTo(newArticle);
    }

    @DisplayName("게시글 생성 - 실패케이스")
    @Test
    public void createArticleFail() throws Exception {
        Article article = Article.builder()
            .category(ArticleType.Category.FOOD.name())
            .userId(userId)
            .title(title)
            .content(content)
            .build();

        Article newArticle = articleRepository.save(article);

        assertThat(newArticle).isNull();
        assertThat(newArticle).isNotInstanceOf(Article.class);
    }

}
