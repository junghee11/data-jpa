package com.develop.datajpa.querydsl;

import com.develop.datajpa.entity.Food;
import com.develop.datajpa.entity.QFood;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class QuerydslApplicationTest {

    @Autowired
    EntityManager em;

    @BeforeEach
    public void before() {
        Food food = new Food(1, "name", "description", 5000);
        em.persist(food);
    }

    @Test
    void contextLoads() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QFood f = new QFood("f");

        Food findFood = query.select(f).from(f).where(f.name.eq("name")).fetchOne();

        Assertions.assertEquals(findFood.getName(), "name");
    }
}
