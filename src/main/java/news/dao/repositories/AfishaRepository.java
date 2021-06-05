package news.dao.repositories;

import news.HibernateUtil;
import news.dao.connection.ConnectionPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Afisha;
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

public class AfishaRepository implements ExtendRepository<Afisha> {
    final private ConnectionPool connectionPool;

    public AfishaRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Afisha> query(ExtendSqlSpecification<Afisha> afishaSpecification) throws SQLException {
        List<Afisha> queryResult = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        if (afishaSpecification.isById()) {
            Afisha afisha = session.get(Afisha.class, (int) afishaSpecification.getCriterial());
            queryResult.add(afisha);
        } else {
            if (afishaSpecification.getCriterial() != null) {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Afisha> criteriaQuery = criteriaBuilder.createQuery(Afisha.class);
                Root<Afisha> root = criteriaQuery.from(Afisha.class);
                ParameterExpression<String> title = criteriaBuilder.parameter(String.class);
                // запрос
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("title"), title));
                Query<Afisha> query = session.createQuery(criteriaQuery);
                query.setParameter(title, (String) afishaSpecification.getCriterial());
                queryResult = query.getResultList();
            } else {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Afisha> criteriaQuery = criteriaBuilder.createQuery(Afisha.class);
                Root<Afisha> root = criteriaQuery.from(Afisha.class);
                // запрос
                criteriaQuery.select(root);
                Query<Afisha> query = session.createQuery(criteriaQuery);
                queryResult = query.getResultList();
            }
        }
        return queryResult;
    }

    @Override
    public int create(Afisha afisha) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(afisha);
        transaction.commit();
        session.close();
        return afisha.getAfishaId();
    }

    @Override
    public int delete(int id) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Afisha afisha = new Afisha();
        afisha.setAfishaId(id);
        try {
            session.delete(afisha);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return id;
    }

    @Override
    public int update(Afisha afisha) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(afisha);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return afisha.getAfishaId();
    }
}
