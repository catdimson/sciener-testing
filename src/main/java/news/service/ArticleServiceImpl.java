package news.service;

import news.dao.repositories.ArticleRepository;
import news.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {

    protected ArticleRepository articleRepository;

    public ArticleServiceImpl() {}

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    @Override
    public List<Article> findByTitle(String title) {
        return articleRepository.findByTitle(title);
    }

    @Override
    public Optional<Article> findById(int id) {
        return articleRepository.findById(id);
    }

    @Override
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public Article updateArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public void deleteArticle(int id) {
        articleRepository.deleteById(id);
    }
}
