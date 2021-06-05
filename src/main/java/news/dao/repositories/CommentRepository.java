package news.dao.repositories;

import news.HibernateUtil;
import news.dao.connection.ConnectionPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Comment;
import news.model.CommentAttachment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommentRepository implements ExtendRepository<Comment> {
    final private ConnectionPool connectionPool;

    public CommentRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

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
        int beChange = 0;
        Connection connection = this.connectionPool.getConnection();
        String sqlDeleteInstance = "DELETE FROM attachment WHERE comment_id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteInstance);
        preparedStatement.setInt(1, id);
        beChange = preparedStatement.executeUpdate();
        String sqlDeleteComment = "DELETE FROM comment WHERE id=?;";
        preparedStatement = connection.prepareStatement(sqlDeleteComment);
        preparedStatement.setInt(1, id);
        beChange = preparedStatement.executeUpdate() | beChange;
        return beChange;
    }

    @Override
    public int update(Comment comment) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlQueryAttachments = "SELECT * FROM attachment WHERE comment_id=?;";
        Object[] instanceComment = comment.getObjects();
        int beChange = 0;

        PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryAttachments, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        preparedStatement.setInt(1, (int) instanceComment[0]);
        ResultSet result = preparedStatement.executeQuery();
        ArrayList<CommentAttachment> attachments = (ArrayList<CommentAttachment>) instanceComment[6];
        Set<CommentAttachment> attachmentsSet = new HashSet<>(attachments);

        outer:
        while (!result.wasNull() && result.next()) {
            for (CommentAttachment attachment : attachments) {
                Object[] instanceAttachment = attachment.getObjects();
                if (result.getInt("id") == (int) instanceAttachment[0]) {
                    // обновляем записи в БД
                    attachmentsSet.remove(attachment);
                    result.updateString(2, (String) instanceAttachment[1]);
                    result.updateString(3, (String) instanceAttachment[2]);
                    result.updateRow();
                    beChange = 1;
                    //statement.executeUpdate(sqlUpdateAttachment);
                    continue outer;
                }
            }
            // удаляем записи из БД
            result.deleteRow();
            beChange = 1;
        }

        // добавляем записи в БД
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ArrayList<CommentAttachment> addingInDBAttachments = new ArrayList<>(attachmentsSet);
        StringBuilder sqlCreateAttachments = new StringBuilder("INSERT INTO attachment (title, path, comment_id) VALUES ");
        for (int i = 0; i < addingInDBAttachments.size(); i++) {
            CommentAttachment attachment = addingInDBAttachments.get(i);
            Object[] attachmentInstance = attachment.getObjects();
            String sqlPath;
            if (i != addingInDBAttachments.size() - 1) {
                sqlPath = String.format("('%s', '%s', %s), ", attachmentInstance[1], attachmentInstance[2], attachmentInstance[3]);
            } else {
                sqlPath = String.format("('%s', '%s', %s); ", attachmentInstance[1], attachmentInstance[2], attachmentInstance[3]);
            }
            sqlCreateAttachments.append(sqlPath);
        }
        beChange = statement.executeUpdate(String.valueOf(sqlCreateAttachments)) | beChange;

        // обновляем запись комментария
        LocalDate createDate = (LocalDate) instanceComment[2];
        LocalDate editDate = (LocalDate) instanceComment[3];
        String sqlUpdateComment = "UPDATE comment SET text=?, create_date=?, edit_date=?, article_id=?, user_id=? WHERE id=?;";
        preparedStatement = connection.prepareStatement(sqlUpdateComment);
        preparedStatement.setString(1, (String) instanceComment[1]);
        preparedStatement.setTimestamp(2, Timestamp.valueOf(createDate.atStartOfDay()));
        preparedStatement.setTimestamp(3, Timestamp.valueOf(editDate.atStartOfDay()));
        preparedStatement.setInt(4, (int) instanceComment[4]);
        preparedStatement.setInt(5, (int) instanceComment[5]);
        preparedStatement.setInt(6, (int) instanceComment[0]);
        beChange = preparedStatement.executeUpdate() | beChange;
        return beChange;
    }
}
