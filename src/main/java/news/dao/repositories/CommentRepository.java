package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Comment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommentRepository implements ExtendRepository<Comment> {
    final private DBPool connectionPool;

    public CommentRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Comment> query(ExtendSqlSpecification<Comment> commentSpecification) throws SQLException {
        List<Comment> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        String sqlQuery = commentSpecification.toSqlClauses();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        if (commentSpecification.getCriterial() != null) {
            preparedStatement.setInt(1, (int) commentSpecification.getCriterial());
        }
        ResultSet result = preparedStatement.executeQuery();
        // переменная содержит id комментария, который содержит вложенные сущности и с которым работаем в цикле
        int idCommentWithAttachments = 0;
        int indexCurrentCommentInResultQuery = 0;
        if (commentSpecification.isById()) {
            result.next();
            Comment comment = new Comment(
                    result.getInt(1),
                    result.getString(2),
                    result.getTimestamp(3).toLocalDateTime().toLocalDate(),
                    result.getTimestamp(4).toLocalDateTime().toLocalDate(),
                    result.getInt(5),
                    result.getInt(6)
            );
            result.previous();
            while (result.next()) {
                    Comment.CommentAttachment commentAttachment = new Comment.CommentAttachment(
                    result.getInt(7),
                    result.getString(8),
                    result.getString(9),
                    result.getInt(10));
                    comment.addNewAttachment(commentAttachment);
            }
            queryResult.add(comment);
        } else {
            /*while (result.next()) {
                    System.out.println("|" + result.getInt(1) + "|" + result.getString(2) + "|" + result.getTimestamp(3) +
                            "|" + result.getTimestamp(4) + "|" + result.getInt(5) + "|" +  result.getInt(6) + "|" +
                            result.getInt(7) + "|" + result.getString(8) + "|" + result.getString(9) + "|" +
                            result.getInt(10));
                }*/
            while (result.next()) {
                // добавляем в результат комментарии без прикреплений
                if (result.getInt(7) == 0) {
                    Comment commentWithoutAttachments = new Comment(
                            result.getInt(1),
                            result.getString(2),
                            result.getTimestamp(3).toLocalDateTime().toLocalDate(),
                            result.getTimestamp(4).toLocalDateTime().toLocalDate(),
                            result.getInt(5),
                            result.getInt(6)
                    );
                   queryResult.add(commentWithoutAttachments);
                   indexCurrentCommentInResultQuery = queryResult.size() - 1;
                } else {
                    if (result.getInt(1) != idCommentWithAttachments) {
                        idCommentWithAttachments = result.getInt(1);
                        Comment commentWithAttachments = new Comment(
                                result.getInt(1),
                                result.getString(2),
                                result.getTimestamp(3).toLocalDateTime().toLocalDate(),
                                result.getTimestamp(4).toLocalDateTime().toLocalDate(),
                                result.getInt(5),
                                result.getInt(6)
                        );
                        Comment.CommentAttachment commentAttachment = new Comment.CommentAttachment(
                                result.getInt(7),
                                result.getString(8),
                                result.getString(9),
                                result.getInt(10));
                        commentWithAttachments.addNewAttachment(commentAttachment);
                        queryResult.add(commentWithAttachments);
                        indexCurrentCommentInResultQuery = queryResult.size() - 1;
                    } else {
                        Comment.CommentAttachment commentAttachment = new Comment.CommentAttachment(
                                result.getInt(7),
                                result.getString(8),
                                result.getString(9),
                                result.getInt(10));
                        Comment comment = queryResult.get(indexCurrentCommentInResultQuery);
                        comment.addNewAttachment(commentAttachment);
                    }
                }
            }
        }

        return queryResult;
    }

    @Override
    public int create(Comment comment) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        String sqlCreateComment = "INSERT INTO comment " +
                "(text, create_date, edit_date, article_id, user_id) " +
                "VALUES(?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sqlCreateComment, Statement.RETURN_GENERATED_KEYS);
        Object[] instance = comment.getObjects();
        statement.setString(1, (String) instance[1]);
        LocalDate createDate = (LocalDate) instance[2];
        statement.setTimestamp(2, Timestamp.valueOf(createDate.atStartOfDay()));
        LocalDate editDate = (LocalDate) instance[3];
        statement.setTimestamp(3, Timestamp.valueOf(editDate.atStartOfDay()));
        statement.setInt(4, (int) instance[4]);
        statement.setInt(5, (int) instance[5]);
        statement.executeUpdate();

        // добавление вложений к комментариям
        StringBuilder sqlCreateAttachments = new StringBuilder("INSERT INTO attachment (title, path, comment_id) VALUES ");
        Statement statementWithoutParams = connection.createStatement();
        ArrayList attachments = (ArrayList) instance[6];
        for (int i = 0; i < attachments.size(); i++) {
            Comment.CommentAttachment attachment = (Comment.CommentAttachment) attachments.get(i);
            Object[] attachmentInstance = attachment.getObjects();
            String sqlPath;
            if (i != attachments.size() - 1) {
                sqlPath = String.format("('%s', '%s', %s), ", attachmentInstance[1], attachmentInstance[2], attachmentInstance[3]);
            } else {
                sqlPath = String.format("('%s', '%s', %s); ", attachmentInstance[1], attachmentInstance[2], attachmentInstance[3]);
            }
            sqlCreateAttachments.append(sqlPath);
        }
        statementWithoutParams.executeUpdate(String.valueOf(sqlCreateAttachments));
        ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getInt(1);
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
        ArrayList<Comment.CommentAttachment> attachments = (ArrayList<Comment.CommentAttachment>) instanceComment[6];
        Set<Comment.CommentAttachment> attachmentsSet = new HashSet<>(attachments);

        outer:
        while (!result.wasNull() && result.next()) {
            for (Comment.CommentAttachment attachment : attachments) {
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
        ArrayList<Comment.CommentAttachment> addingInDBAttachments = new ArrayList<>(attachmentsSet);
        StringBuilder sqlCreateAttachments = new StringBuilder("INSERT INTO attachment (title, path, comment_id) VALUES ");
        for (int i = 0; i < addingInDBAttachments.size(); i++) {
            Comment.CommentAttachment attachment = addingInDBAttachments.get(i);
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
