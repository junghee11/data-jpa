package com.develop.datajpa.repository;

import com.develop.datajpa.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByCategoryAndStateOrderByCreatedAtDesc(String category, int state, Pageable pageable);

    Optional<Article> findByIdxAndState(long idx, int state);

}
