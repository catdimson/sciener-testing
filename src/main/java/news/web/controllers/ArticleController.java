package news.web.controllers;

import news.service.ArticleService;
import news.web.http.HttpRequest;

public class ArticleController {
    HttpRequest request;
    ArticleService articleService;

    public ArticleController(ArticleService articleService, HttpRequest request) {
        this.articleService = articleService;
        this.request = request;
    }
}
