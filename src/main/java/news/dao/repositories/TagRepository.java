package news.dao.repositories;

import news.HibernateUtil;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Tag;
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

public class TagRepository implements ExtendRepository<Tag> {

    public TagRepository() {}

    @Override
    public List<Tag> query(ExtendSqlSpecification<Tag> tagSpecification) throws SQLException {
        List<Tag> queryResult = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        if (tagSpecification.isById()) {
            Tag tag = session.get(Tag.class, (int) tagSpecification.getCriterial());
            queryResult.add(tag);
        } else {
            if (tagSpecification.getCriterial() != null) {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
                Root<Tag> root = criteriaQuery.from(Tag.class);
                ParameterExpression<String> title = criteriaBuilder.parameter(String.class);
                // запрос
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("title"), title));
                Query<Tag> query = session.createQuery(criteriaQuery);
                query.setParameter(title, (String) tagSpecification.getCriterial());
                queryResult = query.getResultList();
            } else {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
                Root<Tag> root = criteriaQuery.from(Tag.class);
                // запрос
                criteriaQuery.select(root);
                Query<Tag> query = session.createQuery(criteriaQuery);
                queryResult = query.getResultList();
            }
        }
        return queryResult;
    }

    @Override
    public int create(Tag tag) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(tag);
        transaction.commit();
        session.close();
        return tag.getTagId();
    }

    @Override
    public int delete(int id) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Tag tag = new Tag();
        tag.setTagId(id);
        try {
            session.delete(tag);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return id;
    }

    @Override
    public int update(Tag tag) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(tag);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return tag.getTagId();
    }
}
