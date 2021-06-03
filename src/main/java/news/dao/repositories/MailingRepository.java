package news.dao.repositories;

import news.HibernateUtil;
import news.dao.connection.ConnectionPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Mailing;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MailingRepository implements ExtendRepository<Mailing> {
    final private ConnectionPool connectionPool;

    public MailingRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Mailing> query(ExtendSqlSpecification<Mailing> mailingSpecification) throws SQLException {
        List<Mailing> queryResult = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        if (mailingSpecification.isById()) {
            Mailing mailing = session.get(Mailing.class, (int) mailingSpecification.getCriterial());
            queryResult.add(mailing);
        } else {
            if (mailingSpecification.getCriterial() != null) {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Mailing> criteriaQuery = criteriaBuilder.createQuery(Mailing.class);
                Root<Mailing> root = criteriaQuery.from(Mailing.class);
                ParameterExpression<String> email = criteriaBuilder.parameter(String.class);
                // запрос
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("email"), email));
                Query<Mailing> query = session.createQuery(criteriaQuery);
                query.setParameter(email, (String) mailingSpecification.getCriterial());
                queryResult = query.getResultList();
            } else {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Mailing> criteriaQuery = criteriaBuilder.createQuery(Mailing.class);
                Root<Mailing> root = criteriaQuery.from(Mailing.class);
                // запрос
                criteriaQuery.select(root);
                Query<Mailing> query = session.createQuery(criteriaQuery);
                queryResult = query.getResultList();
            }
        }
        session.close();
        return queryResult;
    }

    @Override
    public int create(Mailing mailing) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(mailing);
        session.flush();
        transaction.commit();
        session.close();
        return mailing.getMailingId();
    }

    @Override
    public int delete(int id) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Mailing mailing = new Mailing();
        mailing.setMailingId(id);
        try {
            session.delete(mailing);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return id;
    }

    @Override
    public int update(Mailing mailing) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(mailing);
            session.flush();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return mailing.getMailingId();
    }
}
