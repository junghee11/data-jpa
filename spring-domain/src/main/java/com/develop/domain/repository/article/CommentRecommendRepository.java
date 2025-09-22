package com.develop.domain.repository.article;

import com.develop.domain.entity.article.CommentRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CommentRecommendRepository extends JpaRepository<CommentRecommend, Long> {

    CommentRecommend findByCommentIdAndUserId(long commentId, String userId);

    List<CommentRecommend> findByCommentId(long commentId);

    List<CommentRecommend> findByCommentIdInAndUserId(Set<Long> commentId, String userId);

}
