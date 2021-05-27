package news.dao.repositories;

import news.dao.connection.ConnectionPool;
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
    final private ConnectionPool connectionPool;

    public ArticleRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Article> query(ExtendSqlSpecification<Article> articleSpecification) throws SQLException {
        List<Article> queryResult = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        //Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        boolean isById = articleSpecification.isById();
        String sqlQuery = articleSpecification.toSqlClauses();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        if (isById) {
            preparedStatement.setInt(1, (int) articleSpecification.getCriterial());
            preparedStatement.setInt(2, (int) articleSpecification.getCriterial());
        } else {
            if (articleSpecification.getCriterial() != null) {
                preparedStatement.setString(1, (String) articleSpecification.getCriterial());
                preparedStatement.setString(2, (String) articleSpecification.getCriterial());
            }
        }

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

                // добавили в статью id всех тегов
                while (result.next()) {
                    if (result.getInt(18) == 0) {
                        result.previous();
                        break;
                    }
                    article.addNewTagId(result.getInt(18));
                }

                // добавили в статью все изображения
                while (result.next()) {
                    Article.ArticleImage articleImage = new Article.ArticleImage(
                            result.getInt(12),
                            result.getString(13),
                            result.getString(14),
                            result.getInt(15)
                    );
                    article.addNewImage(articleImage);
                }

                // кладем статью в ответ
                queryResult.add(article);

            }
            else {
                result.previous();
                /*while (result.next()) {
                    System.out.println("|" + result.getInt(1) + "|" + result.getInt(2) + "|" + result.getString(3) + "|" +
                            result.getString(4) + "|" + result.getTimestamp(5) + "|" + result.getTimestamp(6) + "|" +
                            result.getString(7) + "|" + result.getBoolean(8) + "|" + result.getInt(9) + "|" +  result.getInt(10) + "|" +
                            result.getInt(11) + "|" +  result.getInt(12) + "|" + result.getString(13) + "|" + result.getString(14) + "|" +
                            result.getInt(15) + "|" + result.getInt(16) + "|" + result.getInt(17) + "|" + result.getInt(18));
                }*/
                while (result.next()) {
                    // добавление в результат статью
                    if (result.getInt(2) != idCurrentArticle) {
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
                        queryResult.add(article);
                        indexCurrentArticleInResultQuery = queryResult.size() - 1;
                        idCurrentArticle = result.getInt(2);
                    }

                    Article currentArticle = queryResult.get(indexCurrentArticleInResultQuery);
                    // добавление в статью всех изображений
                    if (result.getInt(12) != 0) {
                        Article.ArticleImage articleImage = new Article.ArticleImage(
                                result.getInt(12),
                                result.getString(13),
                                result.getString(14),
                                result.getInt(15)
                        );
                        currentArticle.addNewImage(articleImage);
                    }
                    // добавление все id тегов
                    if (result.getInt(16) != 0) {
                        currentArticle.addNewTagId(result.getInt(18));
                    }
                }
                /*// поиск по заголовку
                while (result.next()) {
                // добавляем в результат статью

                    // ветвь добавления новой статьи
                    if (result.getInt(2) != idCurrentArticle) {
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
                        queryResult.add(article);
                        indexCurrentArticleInResultQuery = queryResult.size() - 1;
                        idCurrentArticle = result.getInt(2);
                        result.previous();

                    // добавляем к существующей статье (которая хранится в списке ответов) изображения и id тэгов
                    }
                        *//*System.out.println("|" + result.getInt(1) + "|" + result.getInt(2) + "|" + result.getString(3) + "|" +
                                result.getString(4) + "|" + result.getTimestamp(5) + "|" + result.getTimestamp(6) + "|" +
                                result.getString(7) + "|" + result.getBoolean(8) + "|" + result.getInt(9) + result.getInt(10) + "|" +
                                result.getInt(11) + result.getInt(12) + "|" + result.getString(13) + "|" + result.getString(14) + "|" +
                                result.getInt(15) + "|" + result.getInt(16) + "|" + result.getInt(17) + "|" + result.getInt(18));*//*
                        Article currentArticle = queryResult.get(indexCurrentArticleInResultQuery);
                        System.out.println("ssddsds");


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

                        }


                        // добавляем id тегов
                        while (result.next()) {
                            if (result.getInt(16) == 0) {
                                result.previous();
                                break;
                            }
                            currentArticle.addNewTagId(result.getInt(18));
                        }





                        //System.out.println("Hello");
                        //while (result.next()){
                        //    System.out.println(result.getMetaData());
                        //}

                }*/
            }
        }
        result.beforeFirst();

        return queryResult;
    }

    @Override
    public int create(Article article) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Object[] instanceArticle = article.getObjects();

        // добавление статьи
        String sqlCreateArticle = "INSERT INTO article " +
                "(title, lead, create_date, edit_date, text, is_published, category_id, user_id, source_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sqlCreateArticle, Statement.RETURN_GENERATED_KEYS);
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
        ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();
        int idInstance = generatedKeys.getInt(1);

        // добавление изображений к статьям
        StringBuilder sqlInsertImages = new StringBuilder("INSERT INTO image (title, path, article_id) VALUES ");
        Statement statementWithoutParams = connection.createStatement();
        List images = (ArrayList) instanceArticle[10];
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
            sqlPath = String.format("(%s, %s),", idInstance, tagId);
            sqlInsertIdTags.append(sqlPath);
        });
        sqlInsertIdTags.replace(sqlInsertIdTags.length()-1, sqlInsertIdTags.length(), ";");
        statementWithoutParams.executeUpdate(String.valueOf(sqlInsertIdTags));
        return idInstance;
    }

    @Override
    public int delete(int id) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        int beChange = 0;
        // удаление изображений
        String sqlDeleteImages = String.format("DELETE FROM image WHERE article_id=?;");
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteImages);
        preparedStatement.setInt(1, id);
        beChange = preparedStatement.executeUpdate() | beChange;
        // удаление связи с тегами из промежуточной таблицы article_tag
        String sqlDeleteTagsId = String.format("DELETE FROM article_tag WHERE article_id=?;");
        preparedStatement = connection.prepareStatement(sqlDeleteTagsId);
        preparedStatement.setInt(1, id);
        beChange = preparedStatement.executeUpdate() | beChange;
        // удаление статьи
        String sqlDeleteArticle = String.format("DELETE FROM article WHERE id=?;");
        preparedStatement = connection.prepareStatement(sqlDeleteArticle);
        preparedStatement.setInt(1, id);
        beChange = preparedStatement.executeUpdate() | beChange;
        return beChange;
    }

    @Override
    public int update(Article article) throws SQLException {
        Connection connection = this.connectionPool.getConnection();
        Statement statement = connection.createStatement();
        Object[] instanceArticle = article.getObjects();
        int beChange = 0;

        // для работы с изображениями
        List<Article.ArticleImage> images = (ArrayList<Article.ArticleImage>) instanceArticle[10];
        Set<Article.ArticleImage> imagesSet = new HashSet<>(images);
        String sqlQueryImages = "SELECT * FROM image WHERE article_id=?;";

        // для работы с id тегов
        //List<Integer> tagsId = (ArrayList<Integer>) instanceArticle[11];
        Set<Integer> tagsIdSet = (HashSet) instanceArticle[11];
        String sqlQueryTags = "SELECT * FROM article_tag WHERE article_id=?;";

        // получение изображений
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryImages, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
                    beChange = 1;
                    continue outer;
                }
            }
            // удаляем записи из БД
            result.deleteRow();
            beChange = 1;
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
            beChange = statement.executeUpdate(String.valueOf(sqlCreateImages)) | beChange;
        }
        //System.out.println("ОБНОВЛЯЕМ СТАТЬЮ: sql запрос на добавление картинок: " + sqlCreateImages);

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
        preparedStatement.setInt(10, (int) instanceArticle[0]);
        beChange = preparedStatement.executeUpdate() | beChange;

        // получение id тегов
        preparedStatement = connection.prepareStatement(sqlQueryTags, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        preparedStatement.setInt(1, (int) instanceArticle[0]);
        result = preparedStatement.executeQuery();

        // работа с тегами
        outer:
        while (!result.wasNull() && result.next()) {
            for (Integer tagId : tagsIdSet) {
                if (result.getInt("tag_id") == tagId) {
                    // оставляем id тегов
                    tagsIdSet.remove(tagId);
                    beChange = 1;
                    continue outer;
                }
            }
            // удаляем id тегов
            result.deleteRow();
            beChange = 1;
        }

        // добавляем id тегов
        if (!tagsIdSet.isEmpty()) {
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
            beChange = statement.executeUpdate(String.valueOf(sqlCreateTagsId)) | beChange;
        }
        return beChange;
    }
}