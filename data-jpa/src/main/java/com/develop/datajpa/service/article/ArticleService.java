package com.develop.datajpa.service.article;

import com.develop.datajpa.dto.article.ArticleDto;
import com.develop.datajpa.dto.article.CommentDto;
import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.User;
import com.develop.datajpa.entity.UserType.Role;
import com.develop.datajpa.entity.article.Article;
import com.develop.datajpa.entity.article.ArticleType.ArticleState;
import com.develop.datajpa.entity.article.ArticleType.CommentState;
import com.develop.datajpa.entity.article.ArticleType.Recommend;
import com.develop.datajpa.entity.article.Comment;
import com.develop.datajpa.entity.article.CommentRecommend;
import com.develop.datajpa.repository.UserRepository;
import com.develop.datajpa.repository.article.ArticleRepository;
import com.develop.datajpa.repository.article.CommentRecommendRepository;
import com.develop.datajpa.repository.article.CommentRepository;
import com.develop.datajpa.request.article.AddCommentRequest;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.request.article.GetArticleListRequest;
import com.develop.datajpa.request.article.GetCommentListRequest;
import com.develop.datajpa.request.article.ModifyArticleRequest;
import com.develop.datajpa.request.article.ToggleCommentRequest;
import com.develop.datajpa.response.ClientException;
import com.develop.datajpa.service.user.UserService;
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

import static com.develop.datajpa.service.security.JwtProvider.resolveToken;
import static com.develop.datajpa.service.security.JwtProvider.validateToken;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentRecommendRepository commentRecommendRepository;

    @Autowired
    EntityManager em;

    public Map<String, Object> getArticleList(GetArticleListRequest request) {
        Page<Article> articles = articleRepository.findByCategoryAndStateOrderByCreatedAtDesc
            (request.getCategory().name(), ArticleState.ACTIVE.ordinal(), PageRequest.of(request.getPage() - 1, 10));

        return Map.of(
            "pageCount", articles.getTotalPages(),
            "result", articles.getContent()
        );
    }

    @Transactional
    public Map<String, Object> getArticle(long id) {
        Article article = articleRepository.findByIdxAndState(id, ArticleState.ACTIVE.ordinal())
            .orElseThrow(() -> new ClientException("삭제되었거나 존재하지 않는 게시글입니다."));

        User user = userRepository.findOptionalByUserId(article.getUserId())
            .orElseThrow(() -> new ClientException("작성자 정보가 확인되지 않습니다."));

        if (isNull(user) || Role.WITHDRAWAL.ordinal() == user.getRole()) {
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

        Article article = articleRepository.findByIdxAndState(request.getId(), ArticleState.ACTIVE.ordinal())
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

        Article article = articleRepository.findByIdxAndState(articleId, ArticleState.ACTIVE.ordinal())
            .orElseThrow(() -> new ClientException("삭제되었거나 존재하지 않는 게시글입니다."));
        if (article.getUserId() != user.getUserId()) {
            throw new ClientException("해당 게시글 작성자가 아닙니다");
        }

        articleRepository.delete(article);

        return Map.of("result", "게시글이 삭제되었습니다.");
    }

    @Transactional
    public Map<String, Object> getCommentList(String token, GetCommentListRequest request) {
        Page<Comment> comments = getComments(request);

        Map<String, User> users = getUserInfo(comments.getContent());

        if (!validateToken(token)) {
            List<CommentDto> result = comments.getContent().stream().map(comment -> {
                return new CommentDto(comment, users.get(comment.getUserId()), null);
            }).collect(Collectors.toList());

            return Map.of(
                "result", result,
                "page", comments.getTotalPages()
            );
        }

        LoginInfo loginInfo = resolveToken(token);

        Map<Long, CommentRecommend> recommends = getCommentRecommends(comments.getContent(), loginInfo.getUserId());

        List<CommentDto> result = comments.getContent().stream().map(comment -> {
            return new CommentDto(comment, users.get(comment.getUserId()), recommends.get(comment.getIdx()));
        }).collect(Collectors.toList());

        return Map.of(
            "result", result,
            "page", comments.getTotalPages()
        );

    }

    private Page<Comment> getComments(GetCommentListRequest request) {
        Page<Comment> comments;
        if (isNull(request.getCommentId())) {
            comments = commentRepository.findByArticleIdxAndStateAndDepth
                (request.getId(), ArticleState.ACTIVE.ordinal(), 0, PageRequest.of(request.getPage() - 1, 10));
        } else {
            comments = commentRepository.findByArticleIdxAndStateAndCommentGroupAndDepth
                (request.getId(), ArticleState.ACTIVE.ordinal(), request.getCommentId(), 1, PageRequest.of(request.getPage() - 1, 10));
        }
        return comments;
    }

    private Map<String, User> getUserInfo(List<Comment> comments) {
        Set<String> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());

        Map<String, User> users = userRepository.findByUserIdIn(userIds).stream()
            .collect(Collectors.toMap(User::getUserId, u -> u));

        return users;
    }

    private Map<Long, CommentRecommend> getCommentRecommends(List<Comment> comments, String userId) {
        Set<Long> commentIds = comments.stream().map(Comment::getIdx).collect(Collectors.toSet());

        Map<Long, CommentRecommend> recommends = commentRecommendRepository.findByCommentIdInAndUserId
            (commentIds, userId).stream().collect(Collectors.toMap(CommentRecommend::getCommentId, c -> c));

        return recommends;
    }

    @Transactional
    public Map<String, Object> addComment(LoginInfo loginInfo, AddCommentRequest request) {
        userService.checkUser(loginInfo.getUserId());

        articleRepository.findByIdxAndState(request.getArticleId(), ArticleState.ACTIVE.ordinal())
            .orElseThrow(() -> new ClientException("삭제되었거나 존재하지 않는 게시글입니다."));

        Comment newComment = Comment.builder()
            .articleIdx(request.getArticleId())
            .commentGroup(nonNull(request.getCommentId()) ? request.getCommentId() : 0)
            .userId(loginInfo.getUserId())
            .content(request.getContent())
            .depth(nonNull(request.getCommentId()) ? 1 : 0)
            .build();
        Comment savedComment = commentRepository.save(newComment);

        return Map.of(
            "message", "댓글 작성이 완료되었습니다.",
            "result", savedComment
        );
    }

    @Transactional
    public Map<String, Object> deleteComment(LoginInfo loginInfo, long commentId) {
        User user = userService.checkUser(loginInfo.getUserId());

        Comment comment = commentRepository.findByArticleIdxAndState(commentId, CommentState.ACTIVE.ordinal())
            .orElseThrow(() -> new ClientException("삭제되었거나 존재하지 않는 댓글입니다."));
        if (comment.getUserId() != user.getUserId()) {
            throw new ClientException("해당 댓글 작성자가 아닙니다");
        }

        comment.setState(CommentState.REMOVED.ordinal());
        comment.updateUp(-comment.getUp());
        comment.updateDown(-comment.getDown());
        commentRepository.save(comment);

        List<CommentRecommend> recommends = commentRecommendRepository.findByCommentId(commentId);
        if (!recommends.isEmpty()) {
            commentRecommendRepository.deleteAll(recommends);
        }

        return Map.of("result", "댓글이 삭제되었습니다.");
    }

    @Transactional
    public Map<String, Object> toggleComment(LoginInfo loginInfo, ToggleCommentRequest request) {
        User user = userService.checkUser(loginInfo.getUserId());

        Comment comment = commentRepository.findByArticleIdxAndState(request.getCommentId(), CommentState.ACTIVE.ordinal())
            .orElseThrow(() -> new ClientException("삭제되었거나 존재하지 않는 댓글입니다."));

        Boolean isUp = request.getRecommend() == Recommend.UP;

        CommentRecommend recommend = commentRecommendRepository.findByCommentIdAndUserId(comment.getIdx(), user.getUserId());
        if (isNull(recommend)) {
            CommentRecommend newRecommend = CommentRecommend.builder()
                .commentId(comment.getIdx())
                .userId(user.getUserId())
                .up(isUp)
                .down(!isUp)
                .build();
            commentRecommendRepository.save(newRecommend);

            return Map.of(
                "comment", comment,
                "recommend", newRecommend
            );

        }

        if (isUp) {
            comment.updateUp(recommend.getUp() ? -1 : 1);
            recommend.setUp(!recommend.getUp());
        } else {
            comment.updateDown(recommend.getDown() ? -1 : 1);
            recommend.setDown(!recommend.getDown());
        }

        commentRepository.save(comment);
        commentRecommendRepository.save(recommend);

        return Map.of(
            "comment", comment,
            "recommend", recommend
        );
    }

}
