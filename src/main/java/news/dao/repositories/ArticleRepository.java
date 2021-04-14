package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Article;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        //Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        boolean isById = articleSpecification.isById();
        String sqlQuery = articleSpecification.toSqlClauses();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, articleSpecification.getId());
        preparedStatement.setInt(2, articleSpecification.getId());
        ResultSet result = preparedStatement.executeQuery();
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
        List images = (ArrayList) instanceArticle[6];
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
        Set tagsId = (HashSet) instanceArticle[11];
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
        // удаление изображений
        String sqlDeleteImages = String.format("DELETE FROM image WHERE article_id=?;");
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteImages);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        // удаление связи с тегами из промежуточной таблицы article_tag
        String sqlDeleteTagsId = String.format("DELETE FROM article_tag WHERE article_id=?;");
        preparedStatement = connection.prepareStatement(sqlDeleteTagsId);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        // удаление статьи
        String sqlDeleteArticle = String.format("DELETE FROM article WHERE id=?;");
        preparedStatement = connection.prepareStatement(sqlDeleteArticle);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(Article article) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instanceArticle = article.getObjects();

        // для работы с изображениями
        List<Article.ArticleImage> images = (ArrayList<Article.ArticleImage>) instanceArticle[10];
        Set<Article.ArticleImage> imagesSet = new HashSet<>(images);
        String sqlQueryImages = "SELECT * FROM image WHERE article_id=?;";

        // для работы с id тегов
        List<Integer> tagsId = (ArrayList<Integer>) instanceArticle[11];
        Set<Integer> tagsIdSet = new HashSet<>(tagsId);
        String sqlQueryTags = "SELECT * FROM article_tag WHERE article_id=?;";

        // получение изображений
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryImages);
        preparedStatement.setInt(1, (int) instanceArticle[0]);
        ResultSet result = preparedStatement.executeQuery();

        // работа с изображениями
        outer:
        while (!result.wasNull() && result.next()) {
            for (Article.ArticleImage image : images) {
                Object[] instanceImage = image.getObjects();
                if (result.getInt("id") == (int) instanceImage[0]) {
                    // обновляем записи в БД
                    imagesSet.remove(image);
                    result.updateString(2, (String) instanceImage[1]);
                    result.updateString(3, (String) instanceImage[2]);
                    result.updateRow();
                    continue outer;
                }
            }
            // удаляем записи из БД
            result.deleteRow();
        }

        // добавляем изображения в БД
        if (!imagesSet.isEmpty()) {
            List<Article.ArticleImage> addingInDBImages = new ArrayList<>(imagesSet);
            StringBuilder sqlCreateImages = new StringBuilder("INSERT INTO image (title, path, article_id) VALUES ");
            for (int i = 0; i < addingInDBImages.size(); i++) {
                Article.ArticleImage image = addingInDBImages.get(i);
                Object[] imageInstance = image.getObjects();
                String sqlPath;
                if (i != addingInDBImages.size() - 1) {
                    sqlPath = String.format("('%s', '%s', %s), ", imageInstance[1], imageInstance[2], imageInstance[3]);
                } else {
                    sqlPath = String.format("('%s', '%s', %s); ", imageInstance[1], imageInstance[2], imageInstance[3]);
                }
                sqlCreateImages.append(sqlPath);
            }
            statement.executeUpdate(String.valueOf(sqlCreateImages));
        }

        // обновляем запись статьи
        LocalDate createDate = (LocalDate) instanceArticle[3];
        LocalDate editDate = (LocalDate) instanceArticle[4];
        String sqlUpdateArticle = "UPDATE article SET " +
                "title=?, lead=?, create_date=?, edit_date=?, text=?, is_published=?, category_id=?, user_id=?, source_id=? WHERE id=?;";
        preparedStatement = connection.prepareStatement(sqlUpdateArticle);
        preparedStatement.setString(1, (String) instanceArticle[1]);
        preparedStatement.setString(2, (String) instanceArticle[2]);
        preparedStatement.setTimestamp(3, Timestamp.valueOf(createDate.atStartOfDay()));
        preparedStatement.setTimestamp(4, Timestamp.valueOf(editDate.atStartOfDay()));
        preparedStatement.setString(5, (String) instanceArticle[5]);
        preparedStatement.setBoolean(6, (Boolean) instanceArticle[6]);
        preparedStatement.setInt(7, (int) instanceArticle[7]);
        preparedStatement.setInt(8, (int) instanceArticle[8]);
        preparedStatement.setInt(9, (int) instanceArticle[9]);
        preparedStatement.executeUpdate(sqlUpdateArticle);

        // получение id тегов
        preparedStatement = connection.prepareStatement(sqlQueryTags);
        preparedStatement.setInt(1, (int) instanceArticle[0]);
        result = preparedStatement.executeQuery();

        // работа с тегами
        outer:
        while (!result.wasNull() && result.next()) {
            for (Integer tagId : tagsId) {
                if (result.getInt("tag_id") == tagId) {
                    // оставляем id тегов
                    tagsIdSet.remove(tagId);
                    continue outer;
                }
            }
            // удаляем id тегов
            result.deleteRow();
        }

        // добавляем id тегов
        if (!imagesSet.isEmpty()) {
            List<Integer> addingInDBTagsId = new ArrayList<>(tagsIdSet);
            StringBuilder sqlCreateTagsId = new StringBuilder("INSERT INTO article_tag (article_id, tag_id) VALUES ");
            for (int i = 0; i < addingInDBTagsId.size(); i++) {
                String sqlPath;
                if (i != addingInDBTagsId.size() - 1) {
                    sqlPath = String.format("(%s, %s), ", instanceArticle[0], addingInDBTagsId.get(i));
                } else {
                    sqlPath = String.format("(%s, %s); ", instanceArticle[0], addingInDBTagsId.get(i));
                }
                sqlCreateTagsId.append(sqlPath);
            }
            statement.executeUpdate(String.valueOf(sqlCreateTagsId));
        }
    }
}