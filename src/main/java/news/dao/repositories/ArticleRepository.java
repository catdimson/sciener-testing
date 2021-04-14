package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Article;
import news.model.Comment;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

public class ArticleRepository implements ExtendRepository<Article> {
    final private DBPool connectionPool;

    public ArticleRepository(DBPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Article> query(ExtendSqlSpecification<Article> articleSpecification) throws SQLException {
        List<Article> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        boolean isById = articleSpecification.isById();
        String sqlQuery = articleSpecification.toSqlClauses();
        ResultSet result = statement.executeQuery(sqlQuery);
        // переменная содержит id статьи, которая содержит вложенные сущности и с которым работаем в цикле
        int idCurrentArticle = 0;
        int indexCurrentArticleInResultQuery = 0;
        boolean hasResult = result.next();
        if (hasResult) {
            if (isById) {
                // поиск по id
                // создали статью
                Article article = new Article(
                        result.getInt("article.id"),
                        result.getString("article.title"),
                        result.getString("lead"),
                        result.getTimestamp("create_date").toLocalDateTime().toLocalDate(),
                        result.getTimestamp("edit_date").toLocalDateTime().toLocalDate(),
                        result.getString("text"),
                        result.getBoolean("is_published"),
                        result.getInt("category_id"),
                        result.getInt("user_id"),
                        result.getInt("source_id")
                );
                result.previous();

                // добавили в статью id всех тегов
                while (result.next()) {
                    if (result.getInt("article_tag.id") == 0) {
                        result.previous();
                        break;
                    }
                    article.addNewTagId(result.getInt("tag_id"));
                }

                // добавили в статью все изображения
                while (result.next()) {
                    Article.ArticleImage articleImage = new Article.ArticleImage(
                            result.getInt("image.id"),
                            result.getString("image.title"),
                            result.getString("path"),
                            result.getInt("image.article_id")
                    );
                    article.addNewImage(articleImage);
                }

                // кладем статью в ответ
                queryResult.add(article);

            } else {

                // поиск по заголовку
                while (result.next()) {
                // добавляем в результат статью

                    // ветвь добавления новой статьи
                    if (result.getInt(1) != idCurrentArticle) {
                    Article article = new Article(
                            result.getInt(2),
                            result.getString(3),
                            result.getString(4),
                            result.getTimestamp(5).toLocalDateTime().toLocalDate(),
                            result.getTimestamp(6).toLocalDateTime().toLocalDate(),
                            result.getString(7),
                            result.getBoolean(8),
                            result.getInt(9),
                            result.getInt(10),
                            result.getInt(11)
                    );
                    result.previous();
                    queryResult.add(article);
                    indexCurrentArticleInResultQuery = queryResult.size() - 1;

                    // добавляем к существующей статье (которая хранится в списке ответов) изображения и id тэгов
                    } else {
                        Article currentArticle = queryResult.get(indexCurrentArticleInResultQuery);
                        result.previous();

                        // добавили в статью все изображения
                        while (result.next()) {
                            if (result.getInt(12) == 0) {
                                result.previous();
                                break;
                            }
                            Article.ArticleImage articleImage = new Article.ArticleImage(
                                    result.getInt(12),
                                    result.getString(13),
                                    result.getString(14),
                                    result.getInt(15)
                            );
                            currentArticle.addNewImage(articleImage);
                        }

                        // добавляем id тегов
                        while (result.next()) {
                            if (result.getInt(16) == 0) {
                                result.previous();
                                break;
                            }
                            currentArticle.addNewTagId(result.getInt(18));
                        }
                    }
                }
            }
        }

        return queryResult;
    }

    @Override
    public void create(Article article) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Object[] instanceArticle = article.getObjects();

        // добавление статьи
        String sqlCreateComment = "INSERT INTO article " +
                "(title, lead, create_date, edit_date, text, is_published, category_id, user_id, source_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sqlCreateComment);
        statement.setString(1, (String) instanceArticle[1]);
        statement.setString(2, (String) instanceArticle[2]);
        LocalDate createDate = (LocalDate) instanceArticle[3];
        statement.setTimestamp(3, Timestamp.valueOf(createDate.atStartOfDay()));
        LocalDate editDate = (LocalDate) instanceArticle[4];
        statement.setTimestamp(4, Timestamp.valueOf(editDate.atStartOfDay()));
        statement.setString(5, (String) instanceArticle[5]);
        statement.setBoolean(6, (Boolean) instanceArticle[6]);
        statement.setInt(7, (int) instanceArticle[7]);
        statement.setInt(8, (int) instanceArticle[8]);
        statement.setInt(9, (int) instanceArticle[9]);
        statement.executeUpdate();

        // добавление изображений к статьям
        StringBuilder sqlInsertImages = new StringBuilder("INSERT INTO image (title, path, article_id) VALUES ");
        Statement statementWithoutParams = connection.createStatement();
        ArrayList images = (ArrayList) instanceArticle[6];
        for (int i = 0; i < images.size(); i++) {
            Article.ArticleImage image = (Article.ArticleImage) images.get(i);
            Object[] instanceImage = image.getObjects();
            String sqlPath;
            if (i != images.size() - 1) {
                sqlPath = String.format("('%s', '%s', %s), ", instanceImage[1], instanceImage[2], instanceImage[3]);
            } else {
                sqlPath = String.format("('%s', '%s', %s); ", instanceImage[1], instanceImage[2], instanceImage[3]);
            }
            sqlInsertImages.append(sqlPath);
        }
        statementWithoutParams.executeUpdate(String.valueOf(sqlInsertImages));

        // добавление id тегов
        StringBuilder sqlInsertIdTags = new StringBuilder("INSERT INTO article_tag (article_id, tag_id) VALUES ");
        HashSet tagsId = (HashSet) instanceArticle[11];
        Stream stream = tagsId.stream();
        stream.forEach((tagId) -> {
            String sqlPath;
            sqlPath = String.format("(%s, %s),", instanceArticle[0], tagId);
            sqlInsertIdTags.append(sqlPath);
        });
        sqlInsertIdTags.replace(sqlInsertIdTags.length()-1, sqlInsertIdTags.length(), ";");
        statementWithoutParams.executeUpdate(String.valueOf(sqlInsertIdTags));
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
    public void update(Article article) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Object[] instanceComment = comment.getObjects();
        ArrayList<Comment.CommentAttachment> attachments = (ArrayList<Comment.CommentAttachment>) instanceComment[6];
        Set<Comment.CommentAttachment> attachmentsSet = new HashSet<>(attachments);
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        String sqlQueryAttachments = String.format("SELECT * FROM attachment WHERE comment_id=%s;", instanceComment[0]);
        ResultSet result = statement.executeQuery(sqlQueryAttachments);

        outer:
        while (!result.wasNull() && result.next()) {
            for (Comment.CommentAttachment attachment : attachments) {
                Object[] instanceAttachment = attachment.getObjects();
                if (result.getInt("id") == (int) instanceAttachment[0]) {
                    // обновляем записи в БД
                    attachmentsSet.remove(attachment);
                    //String sqlUpdateAttachment = String.format("UPDATE attachment " +
                    //        "SET title='%s', path='%s' WHERE id=%s;", instanceAttachment[1], instanceAttachment[2], instanceAttachment[0]);
                    result.updateString(2, (String) instanceAttachment[1]);
                    result.updateString(3, (String) instanceAttachment[2]);
                    result.updateRow();
                    //statement.executeUpdate(sqlUpdateAttachment);
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
        String sqlUpdateComment = String.format("UPDATE comment SET text='%s', create_date='%s', edit_date='%s', article_id=%s, user_id=%s WHERE id=%s;",
                instanceComment[1], Timestamp.valueOf(createDate.atStartOfDay()),
                Timestamp.valueOf(editDate.atStartOfDay()), instanceComment[4], instanceComment[5], instanceComment[0]);
        statement.executeUpdate(sqlUpdateComment);
    }
}