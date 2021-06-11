package news.service;

import news.model.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleService {

    List<Article> findAll();

    List<Article> findByTitle(String title);

    Optional<Article> findById(int id);

    Article createArticle(Article article);

    Article updateArticle(Article article);

    void deleteArticle(int id);

}
