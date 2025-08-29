package com.develop.datajpa.repository.article;

import com.develop.datajpa.entity.article.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByArticleIdxAndDepth(long articleId, int depth, Pageable pageable);

    Optional<Comment> findByIdxAndState(long commentId, int state);

    Page<Comment> findByArticleIdxAndCommentGroupAndDepth(long articleId, long group, int depth, Pageable pageable);

    List<Comment> findByUserIdAndStateOrderByCreatedAtDesc(String userId, int state);

}
