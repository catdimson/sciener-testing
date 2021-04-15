package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.FindByIdArticleSpecification;
import news.dao.specifications.FindByTitleArticleSpecification;
import news.model.Article;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleRepositoryTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;
    // для юзера
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    // для статьи
    private static LocalDate createDateArticle;
    private static LocalDate editDateArticle;
    private static LocalDate date;
    private static int articleUserId;
    private static int commentUserId;

    @BeforeAll
    static void beforeAll() {
        date = LocalDate.of(2020, 5, 20);
        // user (дата входа, дара регистрации)
        lastLogin = LocalDate.of(2020, 5, 20);
        dateJoined = LocalDate.of(2019, 5, 20);
        // article (дата создания, дата редактирования, id юзера создавший новость)
        createDateArticle = LocalDate.of(2019, 6, 25);
        editDateArticle = LocalDate.of(2019, 6, 25);
        articleUserId = 1;
    }

    @BeforeEach
    void setUp() throws SQLException {
        this.container = new PostgreSQLContainer("postgres")
                .withUsername("admin")
                .withPassword("qwerty")
                .withDatabaseName("news");
        this.container.start();
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());
        Statement statement = this.poolConnection.getConnection().createStatement();

        // создание группы
        String sqlCreateTableGroup = "CREATE TABLE IF NOT EXISTS \"group\" (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(40) NOT NULL," +
                "CONSTRAINT group_pk PRIMARY KEY (id)," +
                "CONSTRAINT title_unique_group UNIQUE (title)" +
                ");";
        statement.executeUpdate(sqlCreateTableGroup);
        String sqlInsertInstanceTableGroup = "INSERT INTO \"group\"(title)" +
                "SELECT" +
                "(array['admin', 'editor', 'seo', 'guest'])[iter]" +
                "FROM generate_series(1, 4) as iter;";
        statement.executeUpdate(sqlInsertInstanceTableGroup);

        // создание юзера
        String sqlCreateTableUser = "CREATE TABLE IF NOT EXISTS \"user\"  (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "password character varying(128) NOT NULL," +
                "username character varying(150) NOT NULL," +
                "first_name character varying(150) NOT NULL," +
                "last_name character varying(150)," +
                "email character varying(254) NOT NULL," +
                "last_login timestamp NOT NULL," +
                "date_joined timestamp NOT NULL," +
                "is_superuser boolean NOT NULL DEFAULT false," +
                "is_staff boolean NOT NULL DEFAULT false," +
                "is_active boolean NOT NULL DEFAULT true," +
                "group_id integer NOT NULL," +
                "CONSTRAINT user_pk PRIMARY KEY (id)," +
                "CONSTRAINT username_unique UNIQUE (username)," +
                "CONSTRAINT fk_user_group_id FOREIGN KEY (group_id)" +
                "    REFERENCES \"group\" (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE RESTRICT" +
                ");";
        statement.executeUpdate(sqlCreateTableUser);
        String sqlCreateUser = String.format("INSERT INTO \"user\"" +
                        "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                        "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", "qwerty123", "alex", "Александр", "Колесников", "alex1993@mail.ru",
                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), false, true, true, 2);
        statement.executeUpdate(sqlCreateUser);
        sqlCreateUser = String.format("INSERT INTO \"user\"" +
                        "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
                        "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", "qwerty000", "max", "Максим", "Вердилов", "maxiver@mail.ru",
                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), false, true, true, 2);
        statement.executeUpdate(sqlCreateUser);

        // создание источника
        String sqlCreateTableSource = "CREATE TABLE IF NOT EXISTS source (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(50) NOT NULL," +
                "url character varying(500) NOT NULL," +
                "CONSTRAINT source_pk PRIMARY KEY (id)" +
                ");";
        statement.executeUpdate(sqlCreateTableSource);
        String sqlCreateSource = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');";
        statement.executeUpdate(sqlCreateSource);

        // создание категории
        String sqlCreateTableCategory = "CREATE TABLE IF NOT EXISTS category (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ), " +
                "title character varying(50) NOT NULL, " +
                "CONSTRAINT category_pk PRIMARY KEY (id)," +
                "CONSTRAINT title_unique_category UNIQUE (title));";
        statement.executeUpdate(sqlCreateTableCategory);
        String sqlCreateCategory = "INSERT INTO category (title) VALUES ('Спорт'), ('Политика');";
        statement.executeUpdate(sqlCreateCategory);

        // создание тега
        String sqlCreateTableTag = "CREATE TABLE IF NOT EXISTS tag (" +
                "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "    title character varying(50) NOT NULL," +
                "    CONSTRAINT tag_pk PRIMARY KEY (id)" +
                ");";
        statement.executeUpdate(sqlCreateTableTag);
        String sqlCreateTag = "INSERT INTO tag (title) VALUES ('ufc'), ('смешанные единоборства'), ('макгрегор'), " +
                "('балет'), ('картины');";
        statement.executeUpdate(sqlCreateTag);

        // создание article
        String sqlCreateTableArticle = "CREATE TABLE IF NOT EXISTS article (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(250) NOT NULL," +
                "lead character varying(350) NOT NULL," +
                "create_date timestamp NOT NULL," +
                "edit_date timestamp NOT NULL," +
                "text text NOT NULL," +
                "is_published boolean DEFAULT false," +
                "category_id integer NOT NULL DEFAULT 1," +
                "user_id integer NOT NULL," +
                "source_id integer," +
                "CONSTRAINT article_pk PRIMARY KEY (id)," +
                "CONSTRAINT fk_category FOREIGN KEY (category_id)" +
                "    REFERENCES category (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE RESTRICT," +
                "CONSTRAINT fk_user FOREIGN KEY (user_id)" +
                "    REFERENCES \"user\" (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE RESTRICT," +
                "CONSTRAINT fk_source FOREIGN KEY (source_id)" +
                "    REFERENCES source (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE RESTRICT);" +
                "CREATE INDEX IF NOT EXISTS fk_index_category_id ON article (category_id);" +
                "CREATE INDEX IF NOT EXISTS fk_index_article_user_id ON article (user_id);" +
                "CREATE INDEX IF NOT EXISTS fk_index_source_id ON article (source_id);";
        statement.executeUpdate(sqlCreateTableArticle);

        // создание изображения
        String sqlCreateTableImage = "" +
                "CREATE TABLE IF NOT EXISTS image (" +
                "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "    title character varying(80) NOT NULL," +
                "    path character varying(500) NOT NULL," +
                "    article_id integer NOT NULL," +
                "    CONSTRAINT image_pk PRIMARY KEY (id)," +
                "    CONSTRAINT fk_article FOREIGN KEY (article_id)" +
                "        REFERENCES article (id) MATCH SIMPLE" +
                "        ON UPDATE CASCADE" +
                "        ON DELETE CASCADE" +
                ");" +
                "CREATE INDEX IF NOT EXISTS fk_index_image_article_id ON image (article_id);";
        statement.executeUpdate(sqlCreateTableImage);

        // создание таблицы article_tag
        String sqlCreateTableArticleTag = "CREATE TABLE IF NOT EXISTS article_tag (" +
                "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "    article_id integer NOT NULL," +
                "    tag_id integer NOT NULL," +
                "    CONSTRAINT article_tag_pk PRIMARY KEY (id)," +
                "    CONSTRAINT article_tag_unique UNIQUE (article_id, tag_id)," +
                "    CONSTRAINT fk_new FOREIGN KEY (article_id)" +
                "        REFERENCES article (id) MATCH SIMPLE" +
                "        ON UPDATE CASCADE" +
                "        ON DELETE CASCADE," +
                "    CONSTRAINT fk_tag FOREIGN KEY (tag_id)" +
                "        REFERENCES tag (id) MATCH SIMPLE" +
                "        ON UPDATE CASCADE" +
                "        ON DELETE CASCADE);" +
                "CREATE INDEX IF NOT EXISTS fk_index_new_tag_article_id ON article_tag (article_id);" +
                "CREATE INDEX IF NOT EXISTS fk_index_new_tag_tag_id ON article_tag (tag_id);";
        statement.executeUpdate(sqlCreateTableArticleTag);
    }

    @Test
    void findById() {
        try {
            SoftAssertions soft = new SoftAssertions();
            ArticleRepository articleRepository = new ArticleRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            Article article = new Article("Заголовок 1", "Лид 1", createDateArticle, editDateArticle,
                    "Текст 1", true, 1, 1, 1);
            Article.ArticleImage articleImage1 = new Article.ArticleImage("Изображение 1", "/static/images/image1.png", 1);
            Article.ArticleImage articleImage2 = new Article.ArticleImage("Изображение 2", "/static/images/image2.png", 1);
            article.addNewImage(articleImage1);
            article.addNewImage(articleImage2);
            String sqlInsertArticle = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                    "Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
            statement.executeUpdate(sqlInsertArticle);
            String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
                            "VALUES ('%s', '%s', %d), ('%s', '%s', %d);",
                    "Изображение 1", "/static/images/image1.png", 1,
                    "Изображение 2", "/static/images/image2.png", 1);
            statement.executeUpdate(sqlInsertImages);
            String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
                    "(1, 1), (1, 2), (1, 3);";
            statement.executeUpdate(sqlInsertTagsId);

            FindByIdArticleSpecification findById = new FindByIdArticleSpecification(1);
            List<Article> resultFindByIdArticleList = articleRepository.query(findById);
            Object[] resultFindByIdArticleInstance = resultFindByIdArticleList.get(0).getObjects();
            List images = (ArrayList) resultFindByIdArticleInstance[10];
            List tagsId = new ArrayList((HashSet) resultFindByIdArticleInstance[11]);
            Article.ArticleImage resultFindByIdImage1 = (Article.ArticleImage) images.get(0);
            Article.ArticleImage resultFindByIdImage2 = (Article.ArticleImage) images.get(1);
            int tagId1 = (int) tagsId.get(0);
            int tagId2 = (int) tagsId.get(1);
            int tagId3 = (int) tagsId.get(2);

            soft.assertThat(article)
                    .hasFieldOrPropertyWithValue("title", resultFindByIdArticleInstance[1])
                    .hasFieldOrPropertyWithValue("lead", resultFindByIdArticleInstance[2])
                    .hasFieldOrPropertyWithValue("createDate", resultFindByIdArticleInstance[3])
                    .hasFieldOrPropertyWithValue("editDate", resultFindByIdArticleInstance[4])
                    .hasFieldOrPropertyWithValue("text", resultFindByIdArticleInstance[5])
                    .hasFieldOrPropertyWithValue("isPublished", resultFindByIdArticleInstance[6])
                    .hasFieldOrPropertyWithValue("categoryId", resultFindByIdArticleInstance[7])
                    .hasFieldOrPropertyWithValue("userId", resultFindByIdArticleInstance[8])
                    .hasFieldOrPropertyWithValue("sourceId", resultFindByIdArticleInstance[9]);
            soft.assertAll();
            soft.assertThat(resultFindByIdImage1)
                    .hasFieldOrPropertyWithValue("title", "Изображение 1")
                    .hasFieldOrPropertyWithValue("path", "/static/images/image1.png")
                    .hasFieldOrPropertyWithValue("articleId", 1);
            soft.assertAll();
            soft.assertThat(resultFindByIdImage2)
                    .hasFieldOrPropertyWithValue("title", "Изображение 2")
                    .hasFieldOrPropertyWithValue("path", "/static/images/image2.png")
                    .hasFieldOrPropertyWithValue("articleId", 1);
            soft.assertAll();
            assertThat(tagId1).isEqualTo(1);
            assertThat(tagId2).isEqualTo(2);
            assertThat(tagId3).isEqualTo(3);
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void findByTitle() {
        try {
            SoftAssertions soft = new SoftAssertions();
            ArticleRepository articleRepository = new ArticleRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            // создаем две статьи с одиннаковым title
            Article article1 = new Article("Заголовок 1", "Лид 1", createDateArticle, editDateArticle,
                    "Текст 1", true, 1, 1, 1);
            Article article2 = new Article("Заголовок 1", "Лид 2", createDateArticle, editDateArticle,
                    "Текст 2", true, 2, 2, 2);
            // добавляем к ним по 2 изображения
            Article.ArticleImage articleImage1 = new Article.ArticleImage("Изображение 1", "/static/images/image1.png", 1);
            Article.ArticleImage articleImage2 = new Article.ArticleImage("Изображение 2", "/static/images/image2.png", 1);
            Article.ArticleImage articleImage3 = new Article.ArticleImage("Изображение 3", "/static/images/image3.png", 2);
            Article.ArticleImage articleImage4 = new Article.ArticleImage("Изображение 4", "/static/images/image4.png", 2);
            article1.addNewImage(articleImage1);
            article1.addNewImage(articleImage2);
            article2.addNewImage(articleImage3);
            article2.addNewImage(articleImage4);
            // добавили 2 статьи в БД
            String sqlInsertArticle1 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                    "Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
            statement.executeUpdate(sqlInsertArticle1);
            String sqlInsertArticle2 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                    "Заголовок 1", "Лид 2", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 2", true, 2, 2, 2);
            statement.executeUpdate(sqlInsertArticle2);
            // добавляем по 2 картинки к статьям в БД
            String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
                            "VALUES ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d);",
                    "Изображение 1", "/static/images/image1.png", 1,
                    "Изображение 2", "/static/images/image2.png", 1,
                    "Изображение 3", "/static/images/image3.png", 2,
                    "Изображение 4", "/static/images/image4.png", 2);
            // добавляем по 2 тега к статьям в БД
            statement.executeUpdate(sqlInsertImages);
            String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
                    "(1, 1), (1, 2), (2, 3), (2, 4);";
            statement.executeUpdate(sqlInsertTagsId);

            // выполняем поиск
            FindByTitleArticleSpecification findByTitle = new FindByTitleArticleSpecification("Заголовок 1");
            List<Article> resultFindByTitleArticleList = articleRepository.query(findByTitle);
            Object[] resultFindByIdArticleInstance1 = resultFindByTitleArticleList.get(0).getObjects();
            Object[] resultFindByIdArticleInstance2 = resultFindByTitleArticleList.get(1).getObjects();
            List images1 = (ArrayList) resultFindByIdArticleInstance1[10];
            List tagsId1 = new ArrayList((HashSet) resultFindByIdArticleInstance1[11]);
            List images2 = (ArrayList) resultFindByIdArticleInstance2[10];
            List tagsId2 = new ArrayList((HashSet) resultFindByIdArticleInstance2[11]);
            Article.ArticleImage resultFindByIdImage1 = (Article.ArticleImage) images1.get(0);
            Article.ArticleImage resultFindByIdImage2 = (Article.ArticleImage) images1.get(1);
            Article.ArticleImage resultFindByIdImage3 = (Article.ArticleImage) images2.get(0);
            Article.ArticleImage resultFindByIdImage4 = (Article.ArticleImage) images2.get(1);
            int tagId1 = (int) tagsId1.get(0);
            int tagId2 = (int) tagsId1.get(1);
            int tagId3 = (int) tagsId2.get(0);
            int tagId4 = (int) tagsId2.get(1);

            soft.assertThat(article1)
                    .hasFieldOrPropertyWithValue("title", resultFindByIdArticleInstance1[1])
                    .hasFieldOrPropertyWithValue("lead", resultFindByIdArticleInstance1[2])
                    .hasFieldOrPropertyWithValue("createDate", resultFindByIdArticleInstance1[3])
                    .hasFieldOrPropertyWithValue("editDate", resultFindByIdArticleInstance1[4])
                    .hasFieldOrPropertyWithValue("text", resultFindByIdArticleInstance1[5])
                    .hasFieldOrPropertyWithValue("isPublished", resultFindByIdArticleInstance1[6])
                    .hasFieldOrPropertyWithValue("categoryId", resultFindByIdArticleInstance1[7])
                    .hasFieldOrPropertyWithValue("userId", resultFindByIdArticleInstance1[8])
                    .hasFieldOrPropertyWithValue("sourceId", resultFindByIdArticleInstance1[9]);
            soft.assertAll();
            soft.assertThat(article2)
                    .hasFieldOrPropertyWithValue("title", resultFindByIdArticleInstance2[1])
                    .hasFieldOrPropertyWithValue("lead", resultFindByIdArticleInstance2[2])
                    .hasFieldOrPropertyWithValue("createDate", resultFindByIdArticleInstance2[3])
                    .hasFieldOrPropertyWithValue("editDate", resultFindByIdArticleInstance2[4])
                    .hasFieldOrPropertyWithValue("text", resultFindByIdArticleInstance2[5])
                    .hasFieldOrPropertyWithValue("isPublished", resultFindByIdArticleInstance2[6])
                    .hasFieldOrPropertyWithValue("categoryId", resultFindByIdArticleInstance2[7])
                    .hasFieldOrPropertyWithValue("userId", resultFindByIdArticleInstance2[8])
                    .hasFieldOrPropertyWithValue("sourceId", resultFindByIdArticleInstance2[9]);
            soft.assertAll();
            soft.assertThat(resultFindByIdImage1)
                    .hasFieldOrPropertyWithValue("title", "Изображение 1")
                    .hasFieldOrPropertyWithValue("path", "/static/images/image1.png")
                    .hasFieldOrPropertyWithValue("articleId", 1);
            soft.assertAll();
            soft.assertThat(resultFindByIdImage2)
                    .hasFieldOrPropertyWithValue("title", "Изображение 2")
                    .hasFieldOrPropertyWithValue("path", "/static/images/image2.png")
                    .hasFieldOrPropertyWithValue("articleId", 1);
            soft.assertAll();
            soft.assertThat(resultFindByIdImage3)
                    .hasFieldOrPropertyWithValue("title", "Изображение 3")
                    .hasFieldOrPropertyWithValue("path", "/static/images/image3.png")
                    .hasFieldOrPropertyWithValue("articleId", 2);
            soft.assertAll();
            soft.assertThat(resultFindByIdImage4)
                    .hasFieldOrPropertyWithValue("title", "Изображение 4")
                    .hasFieldOrPropertyWithValue("path", "/static/images/image4.png")
                    .hasFieldOrPropertyWithValue("articleId", 2);
            soft.assertAll();
            assertThat(tagId1).isEqualTo(1);
            assertThat(tagId2).isEqualTo(2);
            assertThat(tagId3).isEqualTo(3);
            assertThat(tagId4).isEqualTo(4);
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void createArticle() {
        try {
            SoftAssertions soft = new SoftAssertions();
            ArticleRepository articleRepository = new ArticleRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            // создаем статью
            Article article = new Article("Заголовок 1", "Лид 1", createDateArticle, editDateArticle,
                    "Текст 1", true, 1, 1, 1);
            // изображения к статьям и id тегов
            Article.ArticleImage articleImage1 = new Article.ArticleImage("Изображение 1", "/static/images/image1.png", 1);
            Article.ArticleImage articleImage2 = new Article.ArticleImage("Изображение 2", "/static/images/image2.png", 1);
            article.addNewImage(articleImage1);
            article.addNewImage(articleImage2);
            article.addNewTagId(1);
            article.addNewTagId(2);

            // добавление в БД
            articleRepository.create(article);

            // сверка данных
            String sqlQueryArticle = "SELECT * FROM article WHERE id=1;";
            ResultSet resultArticle = statement.executeQuery(sqlQueryArticle);
            resultArticle.next();
            // сверяем статью
            soft.assertThat(article)
                    .hasFieldOrPropertyWithValue("title", resultArticle.getString("title"))
                    .hasFieldOrPropertyWithValue("lead", resultArticle.getString("lead"))
                    .hasFieldOrPropertyWithValue("createDate", resultArticle.getTimestamp("create_date").toLocalDateTime().toLocalDate())
                    .hasFieldOrPropertyWithValue("editDate", resultArticle.getTimestamp("edit_date").toLocalDateTime().toLocalDate())
                    .hasFieldOrPropertyWithValue("text", resultArticle.getString("text"))
                    .hasFieldOrPropertyWithValue("isPublished", resultArticle.getBoolean("is_published"))
                    .hasFieldOrPropertyWithValue("categoryId", resultArticle.getInt("category_id"))
                    .hasFieldOrPropertyWithValue("userId", resultArticle.getInt("user_id"))
                    .hasFieldOrPropertyWithValue("sourceId", resultArticle.getInt("source_id"));
            soft.assertAll();
            // сверяем изображаения
            String sqlQueryImages = "SELECT * FROM image WHERE article_id=1;";
            ResultSet resultImages = statement.executeQuery(sqlQueryImages);
            resultImages.next();
            soft.assertThat(articleImage1)
                    .hasFieldOrPropertyWithValue("title", resultImages.getString("title"))
                    .hasFieldOrPropertyWithValue("path", resultImages.getString("path"))
                    .hasFieldOrPropertyWithValue("articleId", resultImages.getInt("article_id"));
            soft.assertAll();
            resultImages.next();
            soft.assertThat(articleImage2)
                    .hasFieldOrPropertyWithValue("title", resultImages.getString("title"))
                    .hasFieldOrPropertyWithValue("path", resultImages.getString("path"))
                    .hasFieldOrPropertyWithValue("articleId", resultImages.getInt("article_id"));
            soft.assertAll();
            // сверяем id тегов
            String sqlQueryIdTags = "SELECT * FROM article_tag WHERE article_id=1;";
            ResultSet resultIdTags = statement.executeQuery(sqlQueryIdTags);
            resultIdTags.next();
            assertThat(resultIdTags.getInt("tag_id")).isEqualTo(1);
            resultIdTags.next();
            assertThat(resultIdTags.getInt("tag_id")).isEqualTo(2);
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void deleteArticle() {
        try {
            ArticleRepository articleRepository = new ArticleRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            // создаем статью которую будем удалять
            Article article = new Article("Заголовок 1", "Лид 1", createDateArticle, editDateArticle,
                    "Текст 1", true, 1, 1, 1);
            // изображения к статьям и id тегов
            Article.ArticleImage articleImage1 = new Article.ArticleImage("Изображение 1", "/static/images/image1.png", 1);
            Article.ArticleImage articleImage2 = new Article.ArticleImage("Изображение 2", "/static/images/image2.png", 1);
            article.addNewImage(articleImage1);
            article.addNewImage(articleImage2);
            article.addNewTagId(1);
            article.addNewTagId(2);
            // добавляем статью в БД
            String sqlInsertArticle = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
                    "Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
            statement.executeUpdate(sqlInsertArticle);
            // добавляем 2 картинки к статье в БД
            String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
                            "VALUES ('%s', '%s', %d), ('%s', '%s', %d);",
                    "Изображение 1", "/static/images/image1.png", 1,
                    "Изображение 2", "/static/images/image2.png", 1);
            // добавляем 2 тега к статье в БД
            statement.executeUpdate(sqlInsertImages);
            String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
                    "(1, 1), (1, 2);";
            statement.executeUpdate(sqlInsertTagsId);

            // выполняем удаление
            articleRepository.delete(1);

            // делаем запросы в БД и проверяем, удалена ли статья
            // получаем статью
            String sqlQueryArticle = "SELECT * FROM article WHERE id=1;";
            ResultSet result = statement.executeQuery(sqlQueryArticle);
            assertThat(result.next()).as("Запись класса Article не была удалена").isFalse();
            // получаем изображения
            String sqlQueryImages = "SELECT * FROM image WHERE article_id=1;";
            result = statement.executeQuery(sqlQueryImages);
            assertThat(result.next()).as("Запись класса Image не была удалена").isFalse();
            // получаем id тегов
            String sqlQueryTagsId = "SELECT * FROM article_tag WHERE article_id=1";
            result = statement.executeQuery(sqlQueryTagsId);
            assertThat(result.next()).as("Запись из таблицы article_tag не была удалена").isFalse();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}

    /*@Test
    void updateComment() {
        try {
            SoftAssertions soft = new SoftAssertions();
            CommentRepository commentRepository = new CommentRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            // данные В БД, которые будем обновлять
            String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                            "VALUES ('%s', '%s', '%s', %s, %s);",
                    "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()), Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
            statement.executeUpdate(sqlInsertComment);
            String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id) " +
                            "VALUES ('%s', '%s', %s), ('%s', '%s', %s);",
                    "Прикрепление 1", "/static/attachments/image1.png", 1,
                    "Прикрепление 2", "/static/attachments/image2.png", 1);
            statement.executeUpdate(sqlInsertAttachments);
            // объект, которым будем обновлять данные в БД
            Comment comment = new Comment(1,"Текст комментария новый", createDateComment.plusDays(1), editDateComment.plusDays(1), 2, 1);
            Comment.CommentAttachment commentAttachment1 = new Comment.CommentAttachment(2,"Прикрепление 2 новое", "/static/attachments/image2_новое.png", 1);
            Comment.CommentAttachment commentAttachment2 = new Comment.CommentAttachment("Прикрепление 3", "/static/attachments/image3.png", 1);
            comment.addNewAttachment(commentAttachment1);
            comment.addNewAttachment(commentAttachment2);

            commentRepository.update(comment);

            String sqlQueryComment = String.format("SELECT * FROM comment WHERE id=%s;", 1);
            String sqlQueryAttachment = String.format("SELECT * FROM attachment WHERE comment_id=%s;", 1);
            ResultSet resultComment = statement.executeQuery(sqlQueryComment);
            resultComment.next();
            soft.assertThat(comment)
                    .hasFieldOrPropertyWithValue("text", resultComment.getString("text"))
                    .hasFieldOrPropertyWithValue("createDate", resultComment.getTimestamp("create_date").toLocalDateTime().toLocalDate())
                    .hasFieldOrPropertyWithValue("editDate", resultComment.getTimestamp("edit_date").toLocalDateTime().toLocalDate())
                    .hasFieldOrPropertyWithValue("articleId", resultComment.getInt("article_id"))
                    .hasFieldOrPropertyWithValue("userId", resultComment.getInt("user_id"));
            soft.assertAll();
            ResultSet resultAttachments = statement.executeQuery(sqlQueryAttachment);
            resultAttachments.next();
            soft.assertThat(commentAttachment1)
                    .hasFieldOrPropertyWithValue("title", resultAttachments.getString("title"))
                    .hasFieldOrPropertyWithValue("path", resultAttachments.getString("path"))
                    .hasFieldOrPropertyWithValue("commentId", resultAttachments.getInt("comment_id"));
            soft.assertAll();
            resultAttachments.next();
            soft.assertThat(commentAttachment2)
                    .hasFieldOrPropertyWithValue("title", resultAttachments.getString("title"))
                    .hasFieldOrPropertyWithValue("path", resultAttachments.getString("path"))
                    .hasFieldOrPropertyWithValue("commentId", resultAttachments.getInt("comment_id"));
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }*/
