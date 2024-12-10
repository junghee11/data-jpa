package com.develop.datajpa.service.article;

import com.develop.datajpa.dto.article.ArticleDto;
import com.develop.datajpa.dto.article.CommentDto;
import com.develop.datajpa.dto.baseball.ReviewDto;
import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.Article;
import com.develop.datajpa.entity.ArticleType.State;
import com.develop.datajpa.entity.Comment;
import com.develop.datajpa.entity.CommentRepository;
import com.develop.datajpa.entity.QArticle;
import com.develop.datajpa.entity.QReview;
import com.develop.datajpa.entity.QUser;
import com.develop.datajpa.entity.User;
import com.develop.datajpa.entity.UserType.Role;
import com.develop.datajpa.repository.ArticleRepository;
import com.develop.datajpa.repository.UserRepository;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.request.article.GetArticleListRequest;
import com.develop.datajpa.request.article.GetCommentListRequest;
import com.develop.datajpa.request.article.ModifyArticleRequest;
import com.develop.datajpa.response.ClientException;
import com.develop.datajpa.service.user.UserService;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    EntityManager em;

    public Map<String, Object> getArticleList(GetArticleListRequest request) {
        Page<Article> articles = articleRepository.findByCategoryAndStateOrderByCreatedAtDesc
            (request.getCategory().name(), State.ACTIVE.ordinal(), PageRequest.of(request.getPage() - 1, 10));

        return Map.of(
            "pageCount", articles.getTotalPages(),
            "result", articles.getContent()
        );
    }

    @Transactional
    public Map<String, Object> getArticle(long id) {
        Article article = articleRepository.findByIdxAndState(id, State.ACTIVE.ordinal())
            .orElseThrow(() -> new ClientException("삭제되었거나 존재하지 않는 게시글입니다."));

        User user = userRepository.findOptionalByUserId(article.getUserId())
            .orElseThrow(() -> new ClientException("작성자 정보가 확인되지 않습니다."));

        if(isNull(user) || Role.WITHDRAWAL.ordinal() == user.getRole()) {
            throw new ClientException("탈퇴처리된 회원의 게시글입니다.");
        }

        article.addViewCount();
        articleRepository.save(article);

        ArticleDto result = new ArticleDto(article, user);

        return Map.of(
            "result", result
        );
    }

    public Map<String, Object> createArticle(LoginInfo loginInfo, CreateArticleRequest request) {
        User user = userService.checkUser(loginInfo.getUserId());

        Article newArticle = Article.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .category(request.getCategory().name())
            .userId(user.getUserId())
            .build();
        Article savedArticle = articleRepository.save(newArticle);

        return Map.of(
            "message", "게시글 작성이 완료되었습니다.",
            "result", savedArticle
        );
    }

    public Map<String, Object> modifyArticle(LoginInfo loginInfo, ModifyArticleRequest request) {
        User user = userService.checkUser(loginInfo.getUserId());

        Article article = articleRepository.findByIdxAndState(request.getId(), State.ACTIVE.ordinal())
            .orElseThrow(() -> new ClientException("삭제되었거나 존재하지 않는 게시글입니다."));
        if (article.getUserId() != user.getUserId()) {
            throw new ClientException("해당 게시글 작성자가 아닙니다");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        articleRepository.save(article);

        return Map.of("result", "게시글이 수정되었습니다.");
    }

    public Map<String, Object> deleteArticle(LoginInfo loginInfo, long articleId) {
        User user = userService.checkUser(loginInfo.getUserId());

        Article article = articleRepository.findByIdxAndState(articleId, State.ACTIVE.ordinal())
            .orElseThrow(() -> new ClientException("삭제되었거나 존재하지 않는 게시글입니다."));
        if (article.getUserId() != user.getUserId()) {
            throw new ClientException("해당 게시글 작성자가 아닙니다");
        }

        articleRepository.delete(article);

        return Map.of("result", "게시글이 삭제되었습니다.");
    }

    @Transactional
    public Map<String, Object> getCommentList(GetCommentListRequest request) {
        Page<Comment> comments = commentRepository.findByArticleIdxAndStateAndDepth
            (request.getId(), State.ACTIVE.ordinal(), 0, PageRequest.of(request.getPage() - 1, 10));
        if (comments.isEmpty()) {
            return Map.of(
                "result", List.of()
            );
        }

        Set<String> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());

        Map<String, User> users = userRepository.findByUserIdIn(userIds).stream()
            .collect(Collectors.toMap(User::getUserId, u -> u));

        List<CommentDto> result = comments.getContent().stream().map(comment -> {
            return new CommentDto(comment, users.get(comment.getUserId()));
        }).collect(Collectors.toList());

        return Map.of(
            "result", result,
            "page", comments.getTotalPages()
        );
    }
}
