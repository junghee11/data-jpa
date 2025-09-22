package com.develop.domain.entity;

import com.develop.domain.entity.article.Article;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class ArticleTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testArticleEntity() {
        List<Article> articles = em.createQuery("select a from Article a", Article.class).getResultList();

        for (Article article : articles) {
            System.out.println("article = " + article.getTitle());
        }
    }
}
