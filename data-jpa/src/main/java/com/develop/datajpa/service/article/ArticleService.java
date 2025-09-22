package com.develop.datajpa.service.article;

import com.develop.domain.dto.article.ArticleDto;
import com.develop.domain.dto.article.CommentDto;
import com.develop.domain.dto.user.LoginInfo;
import com.develop.domain.entity.article.Article;
import com.develop.domain.entity.article.ArticleType.Category;
import com.develop.domain.entity.article.ArticleType.ArticleState;
import com.develop.domain.entity.article.ArticleType.CommentState;
import com.develop.domain.entity.article.ArticleType.Recommend;
import com.develop.domain.entity.article.Comment;
import com.develop.domain.entity.article.CommentRecommend;
import com.develop.domain.entity.user.User;
import com.develop.domain.entity.user.UserType.Role;
import com.develop.domain.repository.article.ArticleRepository;
import com.develop.domain.repository.article.CommentRecommendRepository;
import com.develop.domain.repository.article.CommentRepository;
import com.develop.domain.repository.user.UserRepository;
import com.develop.datajpa.request.article.AddCommentRequest;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.request.article.GetArticleListRequest;
import com.develop.datajpa.request.article.GetCommentListRequest;
import com.develop.datajpa.request.article.ModifyArticleRequest;
import com.develop.datajpa.request.article.ToggleCommentRequest;
import com.develop.core.exception.ClientException;
import com.develop.datajpa.service.image.ImageService;
import com.develop.datajpa.service.user.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final ImageService imageService;

    @Autowired
    EntityManager em;

    public Map<String, Object> getArticleList(GetArticleListRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, 10);
        Page<Article> articles;
        if (Category.ALL.equals(request.getCategory())) {
            articles = articleRepository.findByStateOrderByCreatedAtDesc(ArticleState.ACTIVE.ordinal(), pageable);
        } else {
            articles = articleRepository.findByCategoryAndStateOrderByCreatedAtDesc
                (request.getCategory().name(), ArticleState.ACTIVE.ordinal(), pageable);
        }

        Set<String> userIds = articles.getContent().stream().map(Article::getUserId).collect(Collectors.toSet());

        Map<String, User> users = userRepository.findByUserIdIn(userIds).stream()
            .collect(Collectors.toMap(User::getUserId, u -> u));

        List<ArticleDto> result = articles.getContent().stream().map(article -> {
            return new ArticleDto(article, users.get(article.getUserId()));
        }).toList();

        return Map.of(
            "pageCount", articles.getTotalPages(),
            "result", result
        );
    }

    public Article getArticle(Long id) {
        Article article = articleRepository.findByIdxAndState(id, ArticleState.ACTIVE.ordinal())
            .orElseThrow(() -> new ClientException("삭제되었거나 존재하지 않는 게시글입니다."));
        return article;
    }

    @Transactional
    public Map<String, Object> getArticleContent(long id) {
        Article article = getArticle(id);

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

        Article article = getArticle(request.getId());
        if (!article.getUserId().equals(user.getUserId())) {
            throw new ClientException("해당 게시글 작성자가 아닙니다");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        articleRepository.save(article);

        return Map.of("result", "게시글이 수정되었습니다.");
    }

    public Map<String, Object> deleteArticle(LoginInfo loginInfo, long articleId) {
        User user = userService.checkUser(loginInfo.getUserId());

        Article article = getArticle(articleId);
        if (!article.getUserId().equals(user.getUserId())) {
            throw new ClientException("해당 게시글 작성자가 아닙니다");
        }

        article.setState(ArticleState.REMOVED.ordinal());
        articleRepository.save(article);

        return Map.of("message", "게시글이 삭제되었습니다.");
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
            comments = commentRepository.findByArticleIdxAndDepth
                (request.getId(), 0,
                    PageRequest.of(request.getPage() - 1, 10, Sort.by("createdAt").ascending()));
        } else {
            comments = commentRepository.findByArticleIdxAndCommentGroupAndDepth
                (request.getId(), request.getCommentId(), 1,
                    PageRequest.of(0, 10, Sort.by("createdAt").ascending()));
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

        Article article = getArticle(request.getArticleId());

        Comment newComment = Comment.builder()
            .articleIdx(request.getArticleId())
            .userId(loginInfo.getUserId())
            .content(request.getContent())
            .depth(nonNull(request.getCommentId()) ? 1 : 0)
            .build();
        Comment savedComment = commentRepository.save(newComment);

        savedComment.setCommentGroup(nonNull(request.getCommentId()) ? request.getCommentId() : savedComment.getIdx());
        commentRepository.save(savedComment);

        article.addCommentCount();
        articleRepository.save(article);

        return Map.of(
            "message", "댓글 작성이 완료되었습니다.",
            "result", savedComment
        );
    }

    public Comment getComment(long commentId) {
        Comment comment = commentRepository.findByIdxAndState(commentId, CommentState.ACTIVE.ordinal())
            .orElseThrow(() -> new ClientException("삭제되었거나 존재하지 않는 댓글입니다."));
        return comment;
    }

    @Transactional
    public Map<String, Object> deleteComment(LoginInfo loginInfo, long commentId) {
        User user = userService.checkUser(loginInfo.getUserId());

        Comment comment = getComment(commentId);
        if (!comment.getUserId().equals(user.getUserId())) {
            throw new ClientException("해당 댓글 작성자가 아닙니다");
        }

        comment.setState(CommentState.REMOVED.ordinal());
        commentRepository.save(comment);

        List<CommentRecommend> recommends = commentRecommendRepository.findByCommentId(commentId);
        if (!recommends.isEmpty()) {
            commentRecommendRepository.deleteAll(recommends);
        }

        return Map.of("message", "댓글이 삭제되었습니다.");
    }

    @Transactional
    public Map<String, Object> toggleComment(LoginInfo loginInfo, ToggleCommentRequest request) {
        User user = userService.checkUser(loginInfo.getUserId());

        Comment comment = getComment(request.getCommentId());

        Boolean isUp = request.getRecommend() == Recommend.UP;

        CommentRecommend recommend = commentRecommendRepository.findByCommentIdAndUserId(comment.getIdx(), user.getUserId());
        if (isNull(recommend)) {
            if (isUp) {
                comment.updateUp(1);
            } else {
                comment.updateDown(1);
            }

            commentRepository.save(comment);

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
    
    public Map<String, Object> uploadArticleImage(MultipartFile file) {
        String userImgCategory = "article/";
        String imgUrl = imageService.upload(userImgCategory, file);

        return Map.of(
            "imageUrl", imgUrl
        );
    }

}
