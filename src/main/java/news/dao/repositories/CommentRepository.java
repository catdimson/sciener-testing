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
        Statement statement = connection.createStatement();
        boolean isById = commentSpecification.isById();
        String sqlQuery = commentSpecification.toSqlClauses();
        ResultSet result = statement.executeQuery(sqlQuery);
        // переменная содержит id комментария, который содержит вложенные сущности и с которым работаем в цикле
        int idCommentWithAttachments = 0;
        int indexCurrentCommentInResultQuery = 0;
        if (isById) {
            Comment comment = new Comment(
                    result.getInt(1),
                    result.getString(2),
                    result.getTimestamp(3).toLocalDateTime().toLocalDate(),
                    result.getTimestamp(4).toLocalDateTime().toLocalDate(),
                    result.getInt(5),
                    result.getInt(6)
            );
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
                    }

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

        return queryResult;
    }

    @Override
    public void create(Comment comment) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Object[] instance = comment.getObjects();

        // добавление комментариев
        String sqlCreateComment = "INSERT INTO comment " +
                "(text, create_date, edit_date, article_id, user_id) " +
                "VALUES(?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sqlCreateComment);
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
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlDeleteAttachments = String.format("DELETE FROM attachment WHERE comment_id=%d;", id);
        statement.executeUpdate(sqlDeleteAttachments);
        String sqlDeleteComment = String.format("DELETE FROM comment WHERE id=%d;", id);
        statement.executeUpdate(sqlDeleteComment);
    }

    @Override
    public void update(Comment comment) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Object[] instanceComment = comment.getObjects();
        ArrayList<Comment.CommentAttachment> attachments = (ArrayList<Comment.CommentAttachment>) instanceComment[6];
        Set<Comment.CommentAttachment> attachmentsSet = new HashSet<>(attachments);
        Statement statement = connection.createStatement();
        String sqlQueryAttachments = String.format("SELECT * FROM attachment WHERE comment_id=%s;", instanceComment[0]);
        ResultSet result = statement.executeQuery(sqlQueryAttachments);

        outer:
        while (result.next()) {
            for (Comment.CommentAttachment attachment : attachments) {
                Object[] instanceAttachment = attachment.getObjects();
                if (result.getInt("id") == (int) instanceAttachment[0]) {
                    // обновляем записи в БД
                    attachmentsSet.remove(attachment);
                    String sqlUpdateAttachment = String.format("UPDATE attachment " +
                            "SET title='%s', path='%s' WHERE id=%s;", instanceAttachment[1], instanceAttachment[2], instanceAttachment[0]);
                    statement.executeUpdate(sqlUpdateAttachment);
                    continue outer;
                }
            }
            // удаляем записи из БД
            result.deleteRow();
        }

        // добавляем записи в БД
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
        statement.executeUpdate(String.valueOf(sqlCreateAttachments));

        // обновляем запись комментария
        LocalDate createDate = (LocalDate) instanceComment[2];
        LocalDate editDate = (LocalDate) instanceComment[3];
        String sqlUpdateComment = String.format("UPDATE comment (text, create_date, edit_date, article_id, user_id) " +
                "VALUES('%s', '%s', '%s', %s, %s);", instanceComment[1], Timestamp.valueOf(createDate.atStartOfDay()),
                Timestamp.valueOf(editDate.atStartOfDay()), instanceComment[4], instanceComment[5]);
        statement.executeUpdate(sqlUpdateComment);
    }
}
