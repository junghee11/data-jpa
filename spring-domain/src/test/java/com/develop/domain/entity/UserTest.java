package com.develop.domain.entity;

import com.develop.domain.entity.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testUserEntity() {

        User user1 = User.builder()
                .userId("zena")
                .name("남정희")
                .nickname("제나")
                .phone("010-0000-0000")
                .pw("pwpwpwpw")
                .build();

        User user2 = User.builder()
                .userId("kpoint")
                .name("김경한")
                .nickname("킷득")
                .phone("010-1111-1111")
                .pw("zena123")
                .build();

        em.persist(user1);
        em.persist(user2);

        System.out.println("user1 = " + user1);
        System.out.println("user2 = " + user2);
    }
}
