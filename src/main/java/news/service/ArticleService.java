package news.service;

import news.dao.repositories.ArticleRepository;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Article;

import java.sql.SQLException;
import java.util.List;

public class ArticleService implements Service<Article> {
    final private ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> query(ExtendSqlSpecification<Article> specification) throws SQLException {
        return articleRepository.query(specification);
    }

    @Override
    public int create(Article instance) throws SQLException {
        return articleRepository.create(instance);
    }

    @Override
    public void delete(int id) throws SQLException {
        articleRepository.delete(id);
    }

    @Override
    public void update(Article instance) throws SQLException {
        articleRepository.update(instance);
    }
}
