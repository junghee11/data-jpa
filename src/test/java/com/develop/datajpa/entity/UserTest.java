package com.develop.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class UserTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity() {
//        User user1 = new User("zena", "남정희", "제나", "010-0000-0000", "zena123");
//        User user2 = new User("kpoint", "김경한", "킷득", "010-1111-1111", "zena123");
//        em.persist(user1);
//        em.persist(user2);
//
//        System.out.println("user1 = "+ user1);
//        System.out.println("user2 = "+ user2);
//
//        Article articleA = new Article("daily", "날씨가 춥네요", "오늘도 영하래요", user2);
//        Article articleB = new Article("dev", "java 공부 시작해요", "선배님들 조언 부탁 드려요", user1);
//        Article articleC = new Article("tip", "자취 꿀팁", "정보 공유해요ㅎㅎ", user1);
//        Article articleD = new Article("together", "책상 공구해요", "같이 사실분?", user2);
//        em.persist(articleA);
//        em.persist(articleB);
//        em.persist(articleC);
//        em.persist(articleD);
//
//        em.flush();
//        em.clear();

        List<Article> articles = em.createQuery("select a from Article a", Article.class).getResultList();

        for(Article article : articles) {
            System.out.println("article = "+ article.getTitle());
//            System.out.println("article = "+ article);
//            System.out.println("-> article.writer = "+ article.getUser());
        }
    }
}
