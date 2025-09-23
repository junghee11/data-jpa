package com.develop.domain.entity.article;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "comment_recommend")
public class CommentRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "user_id")
    private String userId;

    private Boolean up;

    private Boolean down;

    public void setUp(Boolean up) {
        this.up = up;
    }

    public void setDown(Boolean down) {
        this.down = down;
    }

    @Builder
    public CommentRecommend(Long commentId, String userId, Boolean up, Boolean down) {
        this.commentId = commentId;
        this.userId = userId;
        this.up = up;
        this.down = down;
    }
}
