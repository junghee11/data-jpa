package com.develop.datajpa.repository;

import com.develop.datajpa.entity.baseball.Review;
import com.develop.datajpa.repository.baseball.ReviewRepository;
import com.develop.datajpa.response.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;

    private Long restaurantsId;
    private int star;
    private String content;
    private String userId;

    @BeforeEach
    void setUp() {
        restaurantsId = 1L;
        star = 5;
        content = "분위기 좋고 맛있어요";
        userId = "user1";
    }

    @DisplayName("식당 리뷰글 생성- success")
    @Test
    public void createReviewSuccess() throws Exception {
        Review review = Review.builder()
            .restaurantsId(restaurantsId)
            .star(star)
            .content(content)
            .userId(userId)
            .build();

        Review newReview = reviewRepository.save(review);

        assertThat(newReview).isNotNull();
        assertThat(newReview).isInstanceOf(Review.class);

        Optional<Review> findReview = reviewRepository.findById(newReview.getIdx());

        assertThat(findReview.get()).isNotNull();
        assertThat(findReview.get()).isEqualTo(newReview);
    }

    @DisplayName("식당 리뷰글 생성 - fail")
    @Test
    public void createReviewFail() throws Exception {
        Review review = Review.builder()
            .restaurantsId(restaurantsId)
            .star(star)
            .content(content)
//            .userId(userId)
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            reviewRepository.save(review);
        }, "userId 값 누락 시 DataIntegrityViolationException 이 발생해야 합니다.");
    }

    @DisplayName("식당 리뷰글 삭제- success")
    @Test
    public void deleteReviewSuccess() throws Exception {
        Review review = reviewRepository.findById(1L)
            .orElseThrow(() -> new ClientException("리뷰가 확인되지 않습니다."));

        reviewRepository.delete(review);

        assertThat(review).isNotNull();

        Optional<Review> deleted = reviewRepository.findById(1L);
        assertThat(deleted.isEmpty()).isTrue();
    }

    @DisplayName("식당 리뷰글 삭제 - fail")
    @Test
    public void deleteReviewFail() throws Exception {
        Review review = reviewRepository.findById(1L)
            .orElseThrow(() -> new ClientException("리뷰가 확인되지 않습니다."));

        reviewRepository.delete(review);

        Optional<Review> deleted = reviewRepository.findById(1L);

        assertThrows(NoSuchElementException.class, () -> {
            reviewRepository.delete(deleted.get());
        }, "존재하지 않는 게시글 삭제 시 NoSuchElementException 이 발생해야 합니다.");
    }

}
