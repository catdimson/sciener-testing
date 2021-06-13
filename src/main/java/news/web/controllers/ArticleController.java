package news.web.controllers;

import news.model.Article;
import news.service.ArticleService;
import news.web.controllers.exceptions.InstanceNotFoundException;
import news.web.controllers.exceptions.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/article")
public class ArticleController {
    ArticleService articleService;

    public ArticleController() {
    }

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping(value = "")
    public List<Article> findAllArticles(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return articleService.findAll();
    }

    @GetMapping(value = "", params = {"title"})
    public List<Article> findArticlesByTitle(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        return articleService.findByTitle(request.getParameter("title"));
    }

    @GetMapping(value = "/{id}")
    public Optional<Article> findArticleById(@PathVariable int id, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        Optional<Article> article = articleService.findById(id);
        if (article.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return article;
    }

    @PostMapping(value = "")
    public void createArticle(@RequestBody Article article, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            articleService.createArticle(article);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateArticle(@RequestBody Article article, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            articleService.updateArticle(article);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteArticle(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            articleService.deleteArticle(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
