package news.dao.repositories;

import news.HibernateUtil;
import news.dao.connection.ConnectionPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Group;
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

public class GroupRepository implements ExtendRepository<Group> {
    final private ConnectionPool connectionPool;

    public GroupRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Group> query(ExtendSqlSpecification<Group> groupSpecification) throws SQLException {
        List<Group> queryResult = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        if (groupSpecification.isById()) {
            Group group = session.get(Group.class, (int) groupSpecification.getCriterial());
            queryResult.add(group);
        } else {
            if (groupSpecification.getCriterial() != null) {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Group> criteriaQuery = criteriaBuilder.createQuery(Group.class);
                Root<Group> root = criteriaQuery.from(Group.class);
                ParameterExpression<String> title = criteriaBuilder.parameter(String.class);
                // запрос
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("title"), title));
                Query<Group> query = session.createQuery(criteriaQuery);
                query.setParameter(title, (String) groupSpecification.getCriterial());
                queryResult = query.getResultList();
            } else {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Group> criteriaQuery = criteriaBuilder.createQuery(Group.class);
                Root<Group> root = criteriaQuery.from(Group.class);
                // запрос
                criteriaQuery.select(root);
                Query<Group> query = session.createQuery(criteriaQuery);
                queryResult = query.getResultList();
            }
        }
        return queryResult;
    }

    @Override
    public int create(Group group) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(group);
        session.flush();
        transaction.commit();
        session.close();
        return group.getGroupId();
    }

    @Override
    public int delete(int id) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Group group = new Group();
        group.setGroupId(id);
        try {
            session.delete(group);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return id;
    }

    @Override
    public int update(Group group) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(group);
            session.flush();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return group.getGroupId();
    }
}
