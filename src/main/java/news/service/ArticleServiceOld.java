//package news.service;
//
//import news.dao.repositories.ArticleRepository;
//import news.dao.specifications.ExtendSqlSpecification;
//import news.model.Article;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.sql.SQLException;
//import java.util.List;
//
//@org.springframework.stereotype.Service
//public class ArticleService implements Service<Article> {
//    final private ArticleRepository articleRepository;
//
//    @Autowired
//    public ArticleService(ArticleRepository articleRepository) {
//        this.articleRepository = articleRepository;
//    }
//
//    @Override
//    public List<Article> query(ExtendSqlSpecification<Article> specification) throws SQLException {
//        return articleRepository.query(specification);
//    }
//
//    @Override
//    public int create(Article instance) throws SQLException {
//        return articleRepository.create(instance);
//    }
//
//    @Override
//    public int delete(int id) throws SQLException {
//        return articleRepository.delete(id);
//    }
//
//    @Override
//    public int update(Article instance) throws SQLException {
//        return articleRepository.update(instance);
//    }
//}
