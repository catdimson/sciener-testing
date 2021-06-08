package news.dao.repositories;

import news.HibernateUtil;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Article;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ArticleRepository implements ExtendRepository<Article> {

    public ArticleRepository() {}

    @Override
    public List<Article> query(ExtendSqlSpecification<Article> articleSpecification) throws SQLException {
        List<Article> queryResult = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        if (articleSpecification.isById()) {
            Article article = session.get(Article.class, (int) articleSpecification.getCriterial());
            if (article != null) {
                queryResult.add(article);
            }
        } else {
            if (articleSpecification.getCriterial() != null) {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Article> criteriaQuery = criteriaBuilder.createQuery(Article.class);
                Root<Article> root = criteriaQuery.from(Article.class);
                ParameterExpression<String> title = criteriaBuilder.parameter(String.class);
                // запрос
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("title"), title));
                Query<Article> query = session.createQuery(criteriaQuery);
                query.setParameter(title, (String) articleSpecification.getCriterial());
                queryResult = query.getResultList();
            } else {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Article> criteriaQuery = criteriaBuilder.createQuery(Article.class);
                Root<Article> root = criteriaQuery.from(Article.class);
                // запрос
                criteriaQuery.select(root);
                Query<Article> query = session.createQuery(criteriaQuery);
                queryResult = query.getResultList();
            }
        }
        return queryResult;
    }

    @Override
    public int create(Article article) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(article);
        transaction.commit();
        session.close();
        return article.getArticleId();
    }

    @Override
    public int delete(int id) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Article article = new Article();
        article.setArticleId(id);
        try {
            session.delete(article);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return id;
    }

    @Override
    public int update(Article article) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(article);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return article.getArticleId();
    }
}