package com.develop.datajpa.service.article;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.Article;
import com.develop.datajpa.entity.ArticleType.State;
import com.develop.datajpa.entity.User;
import com.develop.datajpa.repository.ArticleRepository;
import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.request.article.GetArticleListRequest;
import com.develop.datajpa.request.article.ModifyArticleRequest;
import com.develop.datajpa.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserService userService;

    public Map<String, Object> getArticleList(GetArticleListRequest request) {
        Page<Article> articles = articleRepository.findByCategoryAndStateOrderByCreatedAtDesc
            (request.getCategory().name(), State.ACTIVE.ordinal(), PageRequest.of(request.getPage(), 10));

        return Map.of(
            "pageCount", articles.getTotalPages(),
            "result", articles.getContent()
        );
    }

    public Map<String, Object> getArticle(long id) {
        Article article = articleRepository.findByIdxAndState(id, State.ACTIVE.ordinal())
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "삭제되었거나 존재하지 않는 게시글입니다."));

        article.addViewCount();
        articleRepository.save(article);

        return Map.of(
            "result", article
        );
    }

    public Map<String, Object> createArticle(LoginInfo loginInfo, CreateArticleRequest request) {
        User user = userService.checkUser(loginInfo.getUserId());

        Article newArticle = Article.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .category(request.getCategory().name())
            .user(user)
            .build();
        articleRepository.save(newArticle);

        return Map.of("result", "게시글 작성이 완료되었습니다.");
    }

    public Map<String, Object> modifyArticle(LoginInfo loginInfo, ModifyArticleRequest request) {
        User user = userService.checkUser(loginInfo.getUserId());

        Article article = articleRepository.findByIdxAndState(request.getId(), State.ACTIVE.ordinal())
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "삭제되었거나 존재하지 않는 게시글입니다."));
        if (article.getUser() != user) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "해당 게시글 작성자가 아닙니다");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        articleRepository.save(article);

        return Map.of("result", "게시글 작성이 완료되었습니다.");
    }

    public Map<String, Object> deleteArticle(LoginInfo loginInfo, long articleId) {
        User user = userService.checkUser(loginInfo.getUserId());

        Article article = articleRepository.findByIdxAndState(articleId, State.ACTIVE.ordinal())
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "삭제되었거나 존재하지 않는 게시글입니다."));
        if (article.getUser() != user) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "해당 게시글 작성자가 아닙니다");
        }

        articleRepository.delete(article);

        return Map.of("result", "게시글 작성이 완료되었습니다.");
    }
}
