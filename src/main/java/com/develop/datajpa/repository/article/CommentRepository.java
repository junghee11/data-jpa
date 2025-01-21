package com.develop.datajpa.repository.article;

import com.develop.datajpa.entity.article.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByArticleIdxAndStateAndDepth(long articleId, int state, int depth, Pageable pageable);

    Optional<Comment> findByArticleIdxAndState(long articleId, int state);

    Page<Comment> findByArticleIdxAndStateAndCommentGroupAndDepth(long articleId, int state, long group, int depth, Pageable pageable);

}
