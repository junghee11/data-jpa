package com.develop.datajpa.controller.article;


import com.develop.core.security.jwt.CustomUserDetails;
import com.develop.datajpa.request.article.*;
import com.develop.datajpa.service.article.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("")
    public Map<String, Object> getArticleList(@Valid GetArticleListRequest request) {
        return articleService.getArticleList(request);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getArticle(@PathVariable(value = "id") long id) {
        return articleService.getArticleContent(id);
    }

    @PostMapping("")
    public Map<String, Object> createArticle(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @Valid @RequestBody CreateArticleRequest request) {
        return articleService.createArticle(userDetails.getLoginInfo(), request);
    }

    @PatchMapping("")
    public Map<String, Object> modifyArticle(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @Valid @RequestBody ModifyArticleRequest request) {
        return articleService.modifyArticle(userDetails.getLoginInfo(), request);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteArticle(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable(value = "id") long id) {
        return articleService.deleteArticle(userDetails.getLoginInfo(), id);
    }

    @GetMapping("/comments")
    public Map<String, Object> getCommentList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @Valid GetCommentListRequest request) {
        return articleService.getCommentList(userDetails, request);
    }

    @PostMapping("/comment")
    public Map<String, Object> addComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @Valid @RequestBody AddCommentRequest request) {
        return articleService.addComment(userDetails.getLoginInfo(), request);
    }

    @DeleteMapping("/comment/{id}")
    public Map<String, Object> deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable(value = "id") long id) {
        return articleService.deleteComment(userDetails.getLoginInfo(), id);
    }

    @PatchMapping("/comment")
    public Map<String, Object> toggleComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @Valid @RequestBody ToggleCommentRequest request) {
        return articleService.toggleComment(userDetails.getLoginInfo(), request);
    }

    @PostMapping("/image")
    public Map<String, Object> uploadArticleImage(@RequestPart(value = "file") MultipartFile file) {
        return articleService.uploadArticleImage(file);
    }

}
