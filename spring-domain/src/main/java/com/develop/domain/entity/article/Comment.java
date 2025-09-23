package com.develop.domain.entity.article;

import com.develop.core.exception.ClientException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_idx")
    private Long idx;

    @Column(name = "article_idx")
    private Long articleIdx;

    private String content;

    @Column(name = "user_id")
    private String userId;

    private Integer depth;

    @Column(name = "comment_group")
    private Long commentGroup;

    private Integer state;

    private Integer up;

    private Integer down;

    @Column(name = "comment_count")
    private Integer commentCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void setState(Integer state) {
        this.state = state;
    }

    public void updateUp(Integer amount) {
        if (this.up + amount < 0) {
            throw new ClientException("오류가 발생했습니다.");
        }
        this.up += amount;
    }

    public void updateDown(Integer amount) {
        if (this.down + amount < 0) {
            throw new ClientException("오류가 발생했습니다.");
        }
        this.down += amount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public void setCommentGroup(Long commentGroup) {
        this.commentGroup = commentGroup;
    }

    @Builder
    public Comment(long articleIdx, String content, String userId, Integer depth) {
        this.articleIdx = articleIdx;
        this.content = content;
        this.userId = userId;
        this.depth = depth;
    }
}
