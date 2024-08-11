package com.develop.datajpa.repository;

import com.develop.datajpa.entity.Article;
import com.develop.datajpa.entity.ArticleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @DisplayName("게시글 생성- success")
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

    @DisplayName("게시글 생성 - fail")
    @Test
    public void createArticleFail() throws Exception {
        Article article = Article.builder()
            .category(ArticleType.Category.FOOD.name())
            .userId(userId)
//            .title(title)
            .content(content)
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            articleRepository.save(article);
        }, "중복된 제목으로 삽입 시 DataIntegrityViolationException이 발생해야 합니다.");
    }

}
