package news.web.controllers;

import news.dao.specifications.FindAllArticleSpecification;
import news.dao.specifications.FindByIdArticleSpecification;
import news.dao.specifications.FindByTitleArticleSpecification;
import news.model.Article;
import news.service.ArticleService;
import news.web.controllers.exceptions.InstanceNotFoundException;
import news.web.controllers.exceptions.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

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
    public List<Article> findAllArticles(HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindAllArticleSpecification findAll = new FindAllArticleSpecification();
        return articleService.query(findAll);
    }

    @GetMapping(value = "", params = {"title"})
    public List<Article> findArticlesByTitle(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByTitleArticleSpecification findByTitle = new FindByTitleArticleSpecification(request.getParameter("title"));
        return articleService.query(findByTitle);
    }

    @GetMapping(value = "/{id}")
    public Article findArticleById(@PathVariable int id, HttpServletResponse response) throws SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        FindByIdArticleSpecification findById = new FindByIdArticleSpecification(id);
        List<Article> findByIdArticleList = articleService.query(findById);
        if (findByIdArticleList.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return findByIdArticleList.get(0);
    }

    @PostMapping(value = "")
    public void createArticle(@RequestBody Article article, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            articleService.create(article);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @PutMapping(value = "/{id}")
    public void updateArticle(@RequestBody Article article, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            articleService.update(article);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteArticle(@PathVariable int id, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        try {
            articleService.delete(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ServerErrorException();
        }
    }
}
