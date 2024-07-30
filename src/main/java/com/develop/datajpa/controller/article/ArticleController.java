package com.develop.datajpa.controller.article;


import com.develop.datajpa.request.article.CreateArticleRequest;
import com.develop.datajpa.request.article.GetArticleListRequest;
import com.develop.datajpa.request.article.ModifyArticleRequest;
import com.develop.datajpa.service.article.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.develop.datajpa.service.security.JwtProvider.resolveToken;

@RequiredArgsConstructor
@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("")
    public Map<String, Object> getArticleList(@RequestHeader(value = "Authorization", required = false) String token,
                                              @Valid GetArticleListRequest request) {
        return articleService.getArticleList(request);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getArticle(@RequestHeader(value = "Authorization", required = false) String token,
                                          @PathVariable(value = "id") long id) {
        return articleService.getArticle(id);
    }


    @PostMapping("")
    public Map<String, Object> createArticle(@RequestHeader(value = "Authorization") String token,
                                             @Valid @RequestBody CreateArticleRequest request) {
        return articleService.createArticle(resolveToken(token), request);
    }

    @PatchMapping("")
    public Map<String, Object> modifyArticle(@RequestHeader(value = "Authorization") String token,
                                             @Valid @RequestBody ModifyArticleRequest request) {
        return articleService.modifyArticle(resolveToken(token), request);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteArticle(@RequestHeader(value = "Authorization") String token,
                                             @PathVariable(value = "id") long id) {
        return articleService.deleteArticle(resolveToken(token), id);
    }

}
