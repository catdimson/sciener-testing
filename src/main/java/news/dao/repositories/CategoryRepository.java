package news.dao.repositories;

import news.HibernateUtil;
import news.dao.connection.ConnectionPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Category;
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

public class CategoryRepository implements ExtendRepository<Category> {

    public CategoryRepository() {}

    @Override
    public List<Category> query(ExtendSqlSpecification<Category> categorySpecification) throws SQLException {
        List<Category> queryResult = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        if (categorySpecification.isById()) {
            Category category = session.get(Category.class, (int) categorySpecification.getCriterial());
            queryResult.add(category);
        } else {
            if (categorySpecification.getCriterial() != null) {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
                Root<Category> root = criteriaQuery.from(Category.class);
                ParameterExpression<String> title = criteriaBuilder.parameter(String.class);
                // запрос
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("title"), title));
                Query<Category> query = session.createQuery(criteriaQuery);
                query.setParameter(title, (String) categorySpecification.getCriterial());
                queryResult = query.getResultList();
            } else {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
                Root<Category> root = criteriaQuery.from(Category.class);
                // запрос
                criteriaQuery.select(root);
                Query<Category> query = session.createQuery(criteriaQuery);
                queryResult = query.getResultList();
            }
        }
        return queryResult;
    }

    @Override
    public int create(Category category) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(category);
        transaction.commit();
        session.close();
        return category.getCategoryId();
    }

    @Override
    public int delete(int id) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Category category = new Category();
        category.setCategoryId(id);
        try {
            session.delete(category);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return id;
    }

    @Override
    public int update(Category category) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(category);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return category.getCategoryId();
    }
}
