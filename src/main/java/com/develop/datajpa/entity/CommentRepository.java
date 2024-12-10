package com.develop.datajpa.entity;

import com.develop.datajpa.dto.article.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByArticleIdxAndStateAndDepth(long articleId, int state, int depth, Pageable pageable);

}
