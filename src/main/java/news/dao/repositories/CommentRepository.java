package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Comment;
import news.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        Object[] instance = user.getObjects();
        String sqlCreateInstance = "INSERT INTO \"user\"" +
                "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement statement = connection.prepareStatement(sqlCreateInstance);
        statement.setString(1, (String) instance[1]);
        statement.setString(2, (String) instance[2]);
        statement.setString(3, (String) instance[3]);
        statement.setString(4, (String) instance[4]);
        statement.setString(5, (String) instance[5]);
        LocalDate dateLogin = (LocalDate) instance[6];
        statement.setTimestamp(6, Timestamp.valueOf(dateLogin.atStartOfDay()));
        LocalDate dateJoined = (LocalDate) instance[7];
        statement.setTimestamp(7, Timestamp.valueOf(dateJoined.atStartOfDay()));
        statement.setBoolean(8, (Boolean) instance[8]);
        statement.setBoolean(9, (Boolean) instance[9]);
        statement.setBoolean(10, (Boolean) instance[10]);
        statement.setInt(11, (int) instance[11]);
        statement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        String sqlDeleteInstance = String.format("DELETE FROM \"user\" WHERE id=%d;", id);
        statement.executeUpdate(sqlDeleteInstance);
    }

    @Override
    public void update(Comment comment) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Object[] instance = user.getObjects();
        String sqlUpdateInstance = "UPDATE \"user\" SET " +
                "password=?, username=?, first_name=?, last_name=?, email=?, last_login=?, date_joined=?, is_superuser=?, " +
                "is_staff=?, is_active=?, group_id=? WHERE id=?;";

        PreparedStatement statement = connection.prepareStatement(sqlUpdateInstance);
        statement.setString(1, (String) instance[1]);
        statement.setString(2, (String) instance[2]);
        statement.setString(3, (String) instance[3]);
        statement.setString(4, (String) instance[4]);
        statement.setString(5, (String) instance[5]);
        LocalDate dateLogin = (LocalDate) instance[6];
        statement.setTimestamp(6, Timestamp.valueOf(dateLogin.atStartOfDay()));
        LocalDate dateJoined = (LocalDate) instance[7];
        statement.setTimestamp(7, Timestamp.valueOf(dateJoined.atStartOfDay()));
        statement.setBoolean(8, (Boolean) instance[8]);
        statement.setBoolean(9, (Boolean) instance[9]);
        statement.setBoolean(10, (Boolean) instance[10]);
        statement.setInt(11, (int) instance[11]);
        statement.setInt(12, (int) instance[0]);
        statement.executeUpdate();
    }
}
