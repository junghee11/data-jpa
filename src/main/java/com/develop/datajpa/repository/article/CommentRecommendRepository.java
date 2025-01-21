package com.develop.datajpa.repository.article;

import com.develop.datajpa.entity.article.CommentRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRecommendRepository extends JpaRepository<CommentRecommend, Long> {

    CommentRecommend findByCommentIdAndUserId(long commentId, String userId);

    List<CommentRecommend> findByCommentId(long commentId);

}
