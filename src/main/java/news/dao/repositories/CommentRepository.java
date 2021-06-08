package news.dao.repositories;

import news.HibernateUtil;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Comment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentRepository implements ExtendRepository<Comment> {

    public CommentRepository() {}

    @Override
    public List<Comment> query(ExtendSqlSpecification<Comment> commentSpecification) throws SQLException {
        List<Comment> queryResult = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        if (commentSpecification.isById()) {
            Comment comment = session.get(Comment.class, (int) commentSpecification.getCriterial());
            queryResult.add(comment);
        } else {
            if (commentSpecification.getCriterial() != null) {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);
                Root<Comment> root = criteriaQuery.from(Comment.class);
                ParameterExpression<Integer> userId = criteriaBuilder.parameter(Integer.class);
                // запрос
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("userId"), userId));
                Query<Comment> query = session.createQuery(criteriaQuery);
                query.setParameter(userId, (Integer) commentSpecification.getCriterial());
                queryResult = query.getResultList();
            } else {
                // подготовка
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);
                Root<Comment> root = criteriaQuery.from(Comment.class);
                // запрос
                criteriaQuery.select(root);
                Query<Comment> query = session.createQuery(criteriaQuery);
                queryResult = query.getResultList();
            }
        }
        return queryResult;
    }

    @Override
    public int create(Comment comment) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(comment);
        transaction.commit();
        session.close();
        return comment.getCommentId();
    }

    @Override
    public int delete(int id) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Comment comment = new Comment();
        comment.setCommentId(id);
        try {
            session.delete(comment);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return id;
    }

    @Override
    public int update(Comment comment) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(comment);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            session.close();
            return 0;
        }
        return comment.getCommentId();
    }
}
