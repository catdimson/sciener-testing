package news.dao.repositories;

import news.HibernateUtil;
import news.dao.connection.ConnectionPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Source;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SourceRepository implements ExtendRepository<Source> {
    final private ConnectionPool connectionPool;

    public SourceRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Source> query(ExtendSqlSpecification<Source> sourceSpecification) throws SQLException {
        List<Source> queryResult = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        if (sourceSpecification.isById()) {
            Source source = session.get(Source.class, (int) sourceSpecification.getCriterial());
            queryResult.add(source);
        } else {
            if (sourceSpecification.getCriterial() != null) {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Source> criteriaQuery = criteriaBuilder.createQuery(Source.class);
                Root<Source> root = criteriaQuery.from(Source.class);
                ParameterExpression<String> title = criteriaBuilder.parameter(String.class);
                // запрос
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("title"), title));
                Query<Source> query = session.createQuery(criteriaQuery);
                query.setParameter(title, (String) sourceSpecification.getCriterial());
                queryResult = query.getResultList();
            } else {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Source> criteriaQuery = criteriaBuilder.createQuery(Source.class);
                Root<Source> root = criteriaQuery.from(Source.class);
                // запрос
                criteriaQuery.select(root);
                Query<Source> query = session.createQuery(criteriaQuery);
                queryResult = query.getResultList();
            }
        }
        return queryResult;
    }

    @Override
    public int create(Source source) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(source);
        transaction.commit();
        session.close();
        return source.getSourceId();
    }

    @Override
    public int delete(int id) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Source source = new Source();
        source.setSourceId(id);
        try {
            session.delete(source);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return id;
    }

    @Override
    public int update(Source source) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(source);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return source.getSourceId();
    }
}
