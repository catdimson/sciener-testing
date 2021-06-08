package news.dao.repositories;

import news.HibernateUtil;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository implements ExtendRepository<User> {

    public UserRepository() {}

    @Override
    public List<User> query(ExtendSqlSpecification<User> userSpecification) throws SQLException {
        List<User> queryResult = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        if (userSpecification.isById()) {
            User user = session.get(User.class, (int) userSpecification.getCriterial());
            if (user != null) {
                queryResult.add(user);
            }
        } else {
            if (userSpecification.getCriterial() != null) {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
                Root<User> root = criteriaQuery.from(User.class);
                ParameterExpression<String> firstname = criteriaBuilder.parameter(String.class);
                // запрос
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("firstName"), firstname));
                Query<User> query = session.createQuery(criteriaQuery);
                query.setParameter(firstname, (String) userSpecification.getCriterial());
                queryResult = query.getResultList();
            } else {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
                Root<User> root = criteriaQuery.from(User.class);
                // запрос
                criteriaQuery.select(root);
                Query<User> query = session.createQuery(criteriaQuery);
                queryResult = query.getResultList();
            }
        }
        return queryResult;
    }

    @Override
    public int create(User user) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(user);
        transaction.commit();
        session.close();
        return user.getUserId();
    }

    @Override
    public int delete(int id) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        User user = new User();
        user.setUserId(id);
        try {
            session.delete(user);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return id;
    }

    @Override
    public int update(User user) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(user);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return user.getUserId();
    }
}
