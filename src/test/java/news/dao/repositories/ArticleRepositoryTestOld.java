//package news.dao.repositories;
//
//import news.HibernateUtil;
//import news.dao.connection.DBPool;
//import news.dao.specifications.FindAllArticleSpecification;
//import news.dao.specifications.FindByIdArticleSpecification;
//import news.dao.specifications.FindByTitleArticleSpecification;
//import news.model.Article;
//import news.model.ArticleImage;
//import news.model.Tag;
//import org.assertj.core.api.SoftAssertions;
//import org.assertj.core.util.Arrays;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.testcontainers.containers.PostgreSQLContainer;
//
//import java.sql.*;
//import java.time.LocalDate;
//import java.util.Collection;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class ArticleRepositoryTest {
//    private PostgreSQLContainer container;
//    private DBPool poolConnection;
//    // для юзера
//    private static LocalDate lastLogin;
//    private static LocalDate dateJoined;
//    // для статьи
//    private static LocalDate createDateArticle;
//    private static LocalDate editDateArticle;
//    private static LocalDate date;
//    private static int articleUserId;
//    private static int commentUserId;
//
//    @BeforeAll
//    static void beforeAll() {
//        date = LocalDate.of(2020, 5, 20); // 1589922000
//        // user (дата входа, дара регистрации)
//        lastLogin = LocalDate.of(2020, 5, 20);  // 1589922000
//        dateJoined = LocalDate.of(2019, 5, 20); // 1558299600
//        // article (дата создания, дата редактирования, id юзера создавший новость)
//        createDateArticle = LocalDate.of(2019, 6, 25); // 1561410000
//        editDateArticle = LocalDate.of(2019, 6, 25); // 1561410000
//        articleUserId = 1;
//    }
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        this.container = new PostgreSQLContainer("postgres")
//                .withUsername("admin")
//                .withPassword("qwerty")
//                .withDatabaseName("news");
//        this.container.start();
//
//        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());
//
//        HibernateUtil.setConnectionProperties(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());
//
//        Statement statement = this.poolConnection.getConnection().createStatement();
//
//        // создание группы
//        String sqlCreateTableGroup = "CREATE TABLE IF NOT EXISTS \"group\" (" +
//                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
//                "title character varying(40) NOT NULL," +
//                "CONSTRAINT group_pk PRIMARY KEY (id)," +
//                "CONSTRAINT title_unique_group UNIQUE (title)" +
//                ");";
//        statement.executeUpdate(sqlCreateTableGroup);
//        String sqlInsertInstanceTableGroup = "INSERT INTO \"group\"(title)" +
//                "SELECT" +
//                "(array['admin', 'editor', 'seo', 'guest'])[iter]" +
//                "FROM generate_series(1, 4) as iter;";
//        statement.executeUpdate(sqlInsertInstanceTableGroup);
//
//        // создание юзера
//        String sqlCreateTableUser = "CREATE TABLE IF NOT EXISTS \"user\"  (" +
//                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
//                "password character varying(128) NOT NULL," +
//                "username character varying(150) NOT NULL," +
//                "first_name character varying(150) NOT NULL," +
//                "last_name character varying(150)," +
//                "email character varying(254) NOT NULL," +
//                "last_login timestamp NOT NULL," +
//                "date_joined timestamp NOT NULL," +
//                "is_superuser boolean NOT NULL DEFAULT false," +
//                "is_staff boolean NOT NULL DEFAULT false," +
//                "is_active boolean NOT NULL DEFAULT true," +
//                "group_id integer NOT NULL," +
//                "CONSTRAINT user_pk PRIMARY KEY (id)," +
//                "CONSTRAINT username_unique UNIQUE (username)," +
//                "CONSTRAINT fk_user_group_id FOREIGN KEY (group_id)" +
//                "    REFERENCES \"group\" (id) MATCH SIMPLE" +
//                "    ON UPDATE CASCADE" +
//                "    ON DELETE RESTRICT" +
//                ");";
//        statement.executeUpdate(sqlCreateTableUser);
//        String sqlCreateUser = String.format("INSERT INTO \"user\"" +
//                        "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
//                        "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", "qwerty123", "alex", "Александр", "Колесников", "alex1993@mail.ru",
//                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), false, true, true, 2);
//        statement.executeUpdate(sqlCreateUser);
//        sqlCreateUser = String.format("INSERT INTO \"user\"" +
//                        "(password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id) " +
//                        "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);", "qwerty000", "max", "Максим", "Вердилов", "maxiver@mail.ru",
//                Timestamp.valueOf(lastLogin.atStartOfDay()), Timestamp.valueOf(dateJoined.atStartOfDay()), false, true, true, 2);
//        statement.executeUpdate(sqlCreateUser);
//
//        // создание источника
//        String sqlCreateTableSource = "CREATE TABLE IF NOT EXISTS source (" +
//                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
//                "title character varying(50) NOT NULL," +
//                "url character varying(500) NOT NULL," +
//                "CONSTRAINT source_pk PRIMARY KEY (id)" +
//                ");";
//        statement.executeUpdate(sqlCreateTableSource);
//        String sqlCreateSource = "INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');";
//        statement.executeUpdate(sqlCreateSource);
//
//        // создание категории
//        String sqlCreateTableCategory = "CREATE TABLE IF NOT EXISTS category (" +
//                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ), " +
//                "title character varying(50) NOT NULL, " +
//                "CONSTRAINT category_pk PRIMARY KEY (id)," +
//                "CONSTRAINT title_unique_category UNIQUE (title));";
//        statement.executeUpdate(sqlCreateTableCategory);
//        String sqlCreateCategory = "INSERT INTO category (title) VALUES ('Спорт'), ('Политика');";
//        statement.executeUpdate(sqlCreateCategory);
//
//        // создание тега
//        String sqlCreateTableTag = "CREATE TABLE IF NOT EXISTS tag (" +
//                "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
//                "    title character varying(50) NOT NULL," +
//                "    CONSTRAINT tag_pk PRIMARY KEY (id)" +
//                ");";
//        statement.executeUpdate(sqlCreateTableTag);
//        String sqlCreateTag = "INSERT INTO tag (title) VALUES ('ufc'), ('смешанные единоборства'), ('макгрегор'), " +
//                "('балет'), ('картины');";
//        statement.executeUpdate(sqlCreateTag);
//
//        // создание article
//        String sqlCreateTableArticle = "CREATE TABLE IF NOT EXISTS article (" +
//                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
//                "title character varying(250) NOT NULL," +
//                "lead character varying(350) NOT NULL," +
//                "create_date timestamp NOT NULL," +
//                "edit_date timestamp NOT NULL," +
//                "text text NOT NULL," +
//                "is_published boolean DEFAULT false," +
//                "category_id integer NOT NULL DEFAULT 1," +
//                "user_id integer NOT NULL," +
//                "source_id integer," +
//                "CONSTRAINT article_pk PRIMARY KEY (id)," +
//                "CONSTRAINT fk_category FOREIGN KEY (category_id)" +
//                "    REFERENCES category (id) MATCH SIMPLE" +
//                "    ON UPDATE CASCADE" +
//                "    ON DELETE RESTRICT," +
//                "CONSTRAINT fk_user FOREIGN KEY (user_id)" +
//                "    REFERENCES \"user\" (id) MATCH SIMPLE" +
//                "    ON UPDATE CASCADE" +
//                "    ON DELETE RESTRICT," +
//                "CONSTRAINT fk_source FOREIGN KEY (source_id)" +
//                "    REFERENCES source (id) MATCH SIMPLE" +
//                "    ON UPDATE CASCADE" +
//                "    ON DELETE RESTRICT);" +
//                "CREATE INDEX IF NOT EXISTS fk_index_category_id ON article (category_id);" +
//                "CREATE INDEX IF NOT EXISTS fk_index_article_user_id ON article (user_id);" +
//                "CREATE INDEX IF NOT EXISTS fk_index_source_id ON article (source_id);";
//        statement.executeUpdate(sqlCreateTableArticle);
//
//        // создание изображения
//        String sqlCreateTableImage = "" +
//                "CREATE TABLE IF NOT EXISTS image (" +
//                "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
//                "    title character varying(80) NOT NULL," +
//                "    path character varying(500) NOT NULL," +
//                "    article_id integer NOT NULL," +
//                "    CONSTRAINT image_pk PRIMARY KEY (id)," +
//                "    CONSTRAINT fk_article FOREIGN KEY (article_id)" +
//                "        REFERENCES article (id) MATCH SIMPLE" +
//                "        ON UPDATE CASCADE" +
//                "        ON DELETE CASCADE" +
//                ");" +
//                "CREATE INDEX IF NOT EXISTS fk_index_image_article_id ON image (article_id);";
//        statement.executeUpdate(sqlCreateTableImage);
//
//        // создание таблицы article_tag
//        String sqlCreateTableArticleTag = "CREATE TABLE IF NOT EXISTS article_tag (" +
//                "    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
//                "    article_id integer NOT NULL," +
//                "    tag_id integer NOT NULL," +
//                "    CONSTRAINT article_tag_pk PRIMARY KEY (id)," +
//                "    CONSTRAINT article_tag_unique UNIQUE (article_id, tag_id)," +
//                "    CONSTRAINT fk_new FOREIGN KEY (article_id)" +
//                "        REFERENCES article (id) MATCH SIMPLE" +
//                "        ON UPDATE CASCADE" +
//                "        ON DELETE CASCADE," +
//                "    CONSTRAINT fk_tag FOREIGN KEY (tag_id)" +
//                "        REFERENCES tag (id) MATCH SIMPLE" +
//                "        ON UPDATE CASCADE" +
//                "        ON DELETE CASCADE);" +
//                "CREATE INDEX IF NOT EXISTS fk_index_new_tag_article_id ON article_tag (article_id);" +
//                "CREATE INDEX IF NOT EXISTS fk_index_new_tag_tag_id ON article_tag (tag_id);";
//        statement.executeUpdate(sqlCreateTableArticleTag);
//    }
//
//    @Test
//    void findById() {
//        try {
//            SoftAssertions soft = new SoftAssertions();
//            ArticleRepository articleRepository = new ArticleRepository();
//            Connection connection = this.poolConnection.getConnection();
//            Statement statement = connection.createStatement();
//            Article article = new Article("Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()),
//                    "Текст 1", true, 1, 1, 1);
//            ArticleImage articleImage1 = new ArticleImage("Изображение 1", "/static/images/image1.png");
//            ArticleImage articleImage2 = new ArticleImage("Изображение 2", "/static/images/image2.png");
//            articleImage1.setArticle(article);
//            articleImage2.setArticle(article);
//            article.addNewImage(articleImage1);
//            article.addNewImage(articleImage2);
//            String sqlInsertArticle = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
//                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
//                    "Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
//            statement.executeUpdate(sqlInsertArticle);
//            String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
//                            "VALUES ('%s', '%s', %d), ('%s', '%s', %d);",
//                    "Изображение 1", "/static/images/image1.png", 1,
//                    "Изображение 2", "/static/images/image2.png", 1);
//            statement.executeUpdate(sqlInsertImages);
//            String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
//                    "(1, 1), (1, 2), (1, 3);";
//            statement.executeUpdate(sqlInsertTagsId);
//
//            FindByIdArticleSpecification findById = new FindByIdArticleSpecification(1);
//            List<Article> resultFindByIdArticleList = articleRepository.query(findById);
//            Object[] resultFindByIdArticleInstance = resultFindByIdArticleList.get(0).getObjects();
//            Collection<ArticleImage> imagesCollection = (Collection<ArticleImage>) resultFindByIdArticleInstance[10];
//            List<Object> images = Arrays.asList(imagesCollection.toArray());
//            Collection<Tag> tagsCollection = (Collection<Tag>) resultFindByIdArticleInstance[11];
//            List<Object> tags = Arrays.asList(tagsCollection.toArray());
//            ArticleImage resultFindByIdImage1 = (ArticleImage) images.get(0);
//            ArticleImage resultFindByIdImage2 = (ArticleImage) images.get(1);
//            Tag tag1 = (Tag) tags.get(0);
//            Tag tag2 = (Tag) tags.get(1);
//            Tag tag3 = (Tag) tags.get(2);
//
//            soft.assertThat(article)
//                    .hasFieldOrPropertyWithValue("title", resultFindByIdArticleInstance[1])
//                    .hasFieldOrPropertyWithValue("lead", resultFindByIdArticleInstance[2])
//                    .hasFieldOrPropertyWithValue("createDate", resultFindByIdArticleInstance[3])
//                    .hasFieldOrPropertyWithValue("editDate", resultFindByIdArticleInstance[4])
//                    .hasFieldOrPropertyWithValue("text", resultFindByIdArticleInstance[5])
//                    .hasFieldOrPropertyWithValue("isPublished", resultFindByIdArticleInstance[6])
//                    .hasFieldOrPropertyWithValue("categoryId", resultFindByIdArticleInstance[7])
//                    .hasFieldOrPropertyWithValue("userId", resultFindByIdArticleInstance[8])
//                    .hasFieldOrPropertyWithValue("sourceId", resultFindByIdArticleInstance[9]);
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage1)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 1")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image1.png");
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage2)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 2")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image2.png");
//            soft.assertAll();
//            assertThat((int) tag1.getObjects()[0]).isEqualTo(1);
//            assertThat((int) tag2.getObjects()[0]).isEqualTo(2);
//            assertThat((int) tag3.getObjects()[0]).isEqualTo(3);
//            this.poolConnection.pullConnection(connection);
//        } catch (SQLException exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    @Test
//    void findByTitle() {
//        try {
//            SoftAssertions soft = new SoftAssertions();
//            ArticleRepository articleRepository = new ArticleRepository();
//            Connection connection = this.poolConnection.getConnection();
//            Statement statement = connection.createStatement();
//            // создаем две статьи с одиннаковым title
//            Article article1 = new Article("Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()),
//                    "Текст 1", true, 1, 1, 1);
//            Article article2 = new Article("Заголовок 1", "Лид 2", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()),
//                    "Текст 2", true, 2, 2, 2);
//            // добавляем к ним по 2 изображения
//            ArticleImage articleImage1 = new ArticleImage("Изображение 1", "/static/images/image1.png");
//            ArticleImage articleImage2 = new ArticleImage("Изображение 2", "/static/images/image2.png");
//            ArticleImage articleImage3 = new ArticleImage("Изображение 3", "/static/images/image3.png");
//            ArticleImage articleImage4 = new ArticleImage("Изображение 4", "/static/images/image4.png");
//            article1.addNewImage(articleImage1);
//            article1.addNewImage(articleImage2);
//            article2.addNewImage(articleImage3);
//            article2.addNewImage(articleImage4);
//            // добавили 2 статьи в БД
//            String sqlInsertArticle1 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
//                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
//                    "Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
//            statement.executeUpdate(sqlInsertArticle1);
//            String sqlInsertArticle2 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
//                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
//                    "Заголовок 1", "Лид 2", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 2", true, 2, 2, 2);
//            statement.executeUpdate(sqlInsertArticle2);
//            // добавляем по 2 картинки к статьям в БД
//            String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
//                            "VALUES ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d);",
//                    "Изображение 1", "/static/images/image1.png", 1,
//                    "Изображение 2", "/static/images/image2.png", 1,
//                    "Изображение 3", "/static/images/image3.png", 2,
//                    "Изображение 4", "/static/images/image4.png", 2);
//            // добавляем по 2 тега к статьям в БД
//            statement.executeUpdate(sqlInsertImages);
//            String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
//                    "(1, 1), (1, 2), (2, 3), (2, 4);";
//            statement.executeUpdate(sqlInsertTagsId);
//
//            // выполняем поиск
//            FindByTitleArticleSpecification findByTitle = new FindByTitleArticleSpecification("Заголовок 1");
//            List<Article> resultFindByTitleArticleList = articleRepository.query(findByTitle);
//            Object[] resultFindByIdArticleInstance1 = resultFindByTitleArticleList.get(0).getObjects();
//            Object[] resultFindByIdArticleInstance2 = resultFindByTitleArticleList.get(1).getObjects();
//
//            Collection<ArticleImage> imagesCollection1 = (Collection<ArticleImage>) resultFindByIdArticleInstance1[10];
//            List<Object> images1 = Arrays.asList(imagesCollection1.toArray());
//            Collection<Tag> tagsCollection1 = (Collection<Tag>) resultFindByIdArticleInstance1[11];
//            List<Object> tags1 = Arrays.asList(tagsCollection1.toArray());
//            Collection<ArticleImage> imagesCollection2 = (Collection<ArticleImage>) resultFindByIdArticleInstance2[10];
//            List<Object> images2 = Arrays.asList(imagesCollection2.toArray());
//            Collection<Tag> tagsCollection2 = (Collection<Tag>) resultFindByIdArticleInstance2[11];
//            List<Object> tags2 = Arrays.asList(tagsCollection2.toArray());
//            ArticleImage resultFindByIdImage1 = (ArticleImage) images1.get(0);
//            ArticleImage resultFindByIdImage2 = (ArticleImage) images1.get(1);
//            ArticleImage resultFindByIdImage3 = (ArticleImage) images2.get(0);
//            ArticleImage resultFindByIdImage4 = (ArticleImage) images2.get(1);
//            Tag tag1 = (Tag) tags1.get(0);
//            Tag tag2 = (Tag) tags1.get(1);
//            Tag tag3 = (Tag) tags2.get(0);
//            Tag tag4 = (Tag) tags2.get(1);
//
//            soft.assertThat(article1)
//                    .hasFieldOrPropertyWithValue("title", resultFindByIdArticleInstance1[1])
//                    .hasFieldOrPropertyWithValue("lead", resultFindByIdArticleInstance1[2])
//                    .hasFieldOrPropertyWithValue("createDate", resultFindByIdArticleInstance1[3])
//                    .hasFieldOrPropertyWithValue("editDate", resultFindByIdArticleInstance1[4])
//                    .hasFieldOrPropertyWithValue("text", resultFindByIdArticleInstance1[5])
//                    .hasFieldOrPropertyWithValue("isPublished", resultFindByIdArticleInstance1[6])
//                    .hasFieldOrPropertyWithValue("categoryId", resultFindByIdArticleInstance1[7])
//                    .hasFieldOrPropertyWithValue("userId", resultFindByIdArticleInstance1[8])
//                    .hasFieldOrPropertyWithValue("sourceId", resultFindByIdArticleInstance1[9]);
//            soft.assertAll();
//            soft.assertThat(article2)
//                    .hasFieldOrPropertyWithValue("title", resultFindByIdArticleInstance2[1])
//                    .hasFieldOrPropertyWithValue("lead", resultFindByIdArticleInstance2[2])
//                    .hasFieldOrPropertyWithValue("createDate", resultFindByIdArticleInstance2[3])
//                    .hasFieldOrPropertyWithValue("editDate", resultFindByIdArticleInstance2[4])
//                    .hasFieldOrPropertyWithValue("text", resultFindByIdArticleInstance2[5])
//                    .hasFieldOrPropertyWithValue("isPublished", resultFindByIdArticleInstance2[6])
//                    .hasFieldOrPropertyWithValue("categoryId", resultFindByIdArticleInstance2[7])
//                    .hasFieldOrPropertyWithValue("userId", resultFindByIdArticleInstance2[8])
//                    .hasFieldOrPropertyWithValue("sourceId", resultFindByIdArticleInstance2[9]);
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage1)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 1")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image1.png");
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage2)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 2")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image2.png");
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage3)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 3")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image3.png");
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage4)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 4")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image4.png");
//            soft.assertAll();
//            assertThat((int) tag1.getObjects()[0]).isEqualTo(1);
//            assertThat((int) tag2.getObjects()[0]).isEqualTo(2);
//            assertThat((int) tag3.getObjects()[0]).isEqualTo(3);
//            assertThat((int) tag4.getObjects()[0]).isEqualTo(4);
//            this.poolConnection.pullConnection(connection);
//        } catch (SQLException exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    @Test
//    void findAll() {
//        try {
//            SoftAssertions soft = new SoftAssertions();
//            ArticleRepository articleRepository = new ArticleRepository();
//            Connection connection = this.poolConnection.getConnection();
//            Statement statement = connection.createStatement();
//            // создаем две статьи с одиннаковым title
//            Article article1 = new Article("Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
//            Article article2 = new Article("Заголовок 2", "Лид 2", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 2", true, 2, 2, 2);
//            Article article3 = new Article("Заголовок 3", "Лид 3", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 3", true, 2, 2, 2);
//            // добавляем к ним по 2 изображения
//            ArticleImage articleImage1 = new ArticleImage("Изображение 1", "/static/images/image1.png");
//            ArticleImage articleImage2 = new ArticleImage("Изображение 2", "/static/images/image2.png");
//            ArticleImage articleImage3 = new ArticleImage("Изображение 3", "/static/images/image3.png");
//            ArticleImage articleImage4 = new ArticleImage("Изображение 4", "/static/images/image4.png");
//            ArticleImage articleImage5 = new ArticleImage("Изображение 5", "/static/images/image5.png");
//            article1.addNewImage(articleImage1);
//            article1.addNewImage(articleImage2);
//            article2.addNewImage(articleImage3);
//            article2.addNewImage(articleImage4);
//            article3.addNewImage(articleImage5);
//            // добавили 2 статьи в БД
//            String sqlInsertArticle1 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
//                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
//                    "Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
//            statement.executeUpdate(sqlInsertArticle1);
//            String sqlInsertArticle2 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
//                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
//                    "Заголовок 2", "Лид 2", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 2", true, 2, 2, 2);
//            statement.executeUpdate(sqlInsertArticle2);
//            String sqlInsertArticle3 = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
//                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
//                    "Заголовок 3", "Лид 3", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 3", true, 2, 2, 2);
//            statement.executeUpdate(sqlInsertArticle3);
//            // добавляем по 2 картинки к статьям в БД
//            String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
//                            "VALUES ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d), ('%s', '%s', %d);",
//                    "Изображение 1", "/static/images/image1.png", 1,
//                    "Изображение 2", "/static/images/image2.png", 1,
//                    "Изображение 3", "/static/images/image3.png", 2,
//                    "Изображение 4", "/static/images/image4.png", 2,
//                    "Изображение 5", "/static/images/image5.png", 3);
//            // добавляем по 2 тега к статьям в БД
//            statement.executeUpdate(sqlInsertImages);
//            String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
//                    "(1, 1), (1, 2), (2, 3), (2, 4), (3, 4), (3, 2);";
//            statement.executeUpdate(sqlInsertTagsId);
//
//            // выполняем поиск
//            FindAllArticleSpecification findAll = new FindAllArticleSpecification();
//            List<Article> resultFindAllArticleList = articleRepository.query(findAll);
//            Object[] resultFindAllArticleInstance1 = resultFindAllArticleList.get(0).getObjects();
//            Object[] resultFindAllArticleInstance2 = resultFindAllArticleList.get(1).getObjects();
//            Object[] resultFindAllArticleInstance3 = resultFindAllArticleList.get(2).getObjects();
//            Collection<ArticleImage> imagesCollection1 = (Collection<ArticleImage>) resultFindAllArticleInstance1[10];
//            List<Object> images1 = Arrays.asList(imagesCollection1.toArray());
//            Collection<Tag> tagsCollection1 = (Collection<Tag>) resultFindAllArticleInstance1[11];
//            List<Object> tags1 = Arrays.asList(tagsCollection1.toArray());
//            Collection<ArticleImage> imagesCollection2 = (Collection<ArticleImage>) resultFindAllArticleInstance2[10];
//            List<Object> images2 = Arrays.asList(imagesCollection2.toArray());
//            Collection<Tag> tagsCollection2 = (Collection<Tag>) resultFindAllArticleInstance2[11];
//            List<Object> tags2 = Arrays.asList(tagsCollection2.toArray());
//            Collection<ArticleImage> imagesCollection3 = (Collection<ArticleImage>) resultFindAllArticleInstance3[10];
//            List<Object> images3 = Arrays.asList(imagesCollection3.toArray());
//            Collection<Tag> tagsCollection3 = (Collection<Tag>) resultFindAllArticleInstance3[11];
//            List<Object> tags3 = Arrays.asList(tagsCollection3.toArray());
//            ArticleImage resultFindByIdImage1 = (ArticleImage) images1.get(0);
//            ArticleImage resultFindByIdImage2 = (ArticleImage) images1.get(1);
//            ArticleImage resultFindByIdImage3 = (ArticleImage) images2.get(0);
//            ArticleImage resultFindByIdImage4 = (ArticleImage) images2.get(1);
//            ArticleImage resultFindByIdImage5 = (ArticleImage) images3.get(0);
//            Tag tag1 = (Tag) tags1.get(0);
//            Tag tag2 = (Tag) tags1.get(1);
//            Tag tag3 = (Tag) tags2.get(0);
//            Tag tag4 = (Tag) tags2.get(1);
//            Tag tag5 = (Tag) tags3.get(0);
//            Tag tag6 = (Tag) tags3.get(1);
//
//            soft.assertThat(article1)
//                    .hasFieldOrPropertyWithValue("title", resultFindAllArticleInstance1[1])
//                    .hasFieldOrPropertyWithValue("lead", resultFindAllArticleInstance1[2])
//                    .hasFieldOrPropertyWithValue("createDate", resultFindAllArticleInstance1[3])
//                    .hasFieldOrPropertyWithValue("editDate", resultFindAllArticleInstance1[4])
//                    .hasFieldOrPropertyWithValue("text", resultFindAllArticleInstance1[5])
//                    .hasFieldOrPropertyWithValue("isPublished", resultFindAllArticleInstance1[6])
//                    .hasFieldOrPropertyWithValue("categoryId", resultFindAllArticleInstance1[7])
//                    .hasFieldOrPropertyWithValue("userId", resultFindAllArticleInstance1[8])
//                    .hasFieldOrPropertyWithValue("sourceId", resultFindAllArticleInstance1[9]);
//            soft.assertAll();
//            soft.assertThat(article2)
//                    .hasFieldOrPropertyWithValue("title", resultFindAllArticleInstance2[1])
//                    .hasFieldOrPropertyWithValue("lead", resultFindAllArticleInstance2[2])
//                    .hasFieldOrPropertyWithValue("createDate", resultFindAllArticleInstance2[3])
//                    .hasFieldOrPropertyWithValue("editDate", resultFindAllArticleInstance2[4])
//                    .hasFieldOrPropertyWithValue("text", resultFindAllArticleInstance2[5])
//                    .hasFieldOrPropertyWithValue("isPublished", resultFindAllArticleInstance2[6])
//                    .hasFieldOrPropertyWithValue("categoryId", resultFindAllArticleInstance2[7])
//                    .hasFieldOrPropertyWithValue("userId", resultFindAllArticleInstance2[8])
//                    .hasFieldOrPropertyWithValue("sourceId", resultFindAllArticleInstance2[9]);
//            soft.assertAll();
//            soft.assertThat(article3)
//                    .hasFieldOrPropertyWithValue("title", resultFindAllArticleInstance3[1])
//                    .hasFieldOrPropertyWithValue("lead", resultFindAllArticleInstance3[2])
//                    .hasFieldOrPropertyWithValue("createDate", resultFindAllArticleInstance3[3])
//                    .hasFieldOrPropertyWithValue("editDate", resultFindAllArticleInstance3[4])
//                    .hasFieldOrPropertyWithValue("text", resultFindAllArticleInstance3[5])
//                    .hasFieldOrPropertyWithValue("isPublished", resultFindAllArticleInstance3[6])
//                    .hasFieldOrPropertyWithValue("categoryId", resultFindAllArticleInstance3[7])
//                    .hasFieldOrPropertyWithValue("userId", resultFindAllArticleInstance3[8])
//                    .hasFieldOrPropertyWithValue("sourceId", resultFindAllArticleInstance3[9]);
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage1)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 1")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image1.png");
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage2)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 2")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image2.png");
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage3)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 3")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image3.png");
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage4)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 4")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image4.png");
//            soft.assertAll();
//            soft.assertThat(resultFindByIdImage5)
//                    .hasFieldOrPropertyWithValue("title", "Изображение 5")
//                    .hasFieldOrPropertyWithValue("path", "/static/images/image5.png");
//            soft.assertAll();
//            assertThat((int) tag1.getObjects()[0]).isEqualTo(1);
//            assertThat((int) tag2.getObjects()[0]).isEqualTo(2);
//            assertThat((int) tag3.getObjects()[0]).isEqualTo(3);
//            assertThat((int) tag4.getObjects()[0]).isEqualTo(4);
//            assertThat((int) tag5.getObjects()[0]).isEqualTo(2);
//            assertThat((int) tag6.getObjects()[0]).isEqualTo(4);
//            this.poolConnection.pullConnection(connection);
//        } catch (SQLException exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    @Test
//    void createArticle() {
//        try {
//            SoftAssertions soft = new SoftAssertions();
//            ArticleRepository articleRepository = new ArticleRepository();
//            Connection connection = this.poolConnection.getConnection();
//            Statement statement = connection.createStatement();
//            // создаем статью
//            Article article = new Article("Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
//            // изображения к статьям и id тегов
//            ArticleImage articleImage1 = new ArticleImage("Изображение 1", "/static/images/image1.png");
//            ArticleImage articleImage2 = new ArticleImage("Изображение 2", "/static/images/image2.png");
//            articleImage1.setArticle(article);
//            articleImage2.setArticle(article);
//            article.addNewImage(articleImage1);
//            article.addNewImage(articleImage2);
//            Tag tag1 = new Tag("new_tag");
//            Tag tag2 = new Tag("new_tag2");
//            tag1.addNewArticle(article);
//            tag2.addNewArticle(article);
//            article.addNewTag(tag1);
//            article.addNewTag(tag2);
//
//            // добавление в БД
//            articleRepository.create(article);
//
//            // сверка данных
//            String sqlQueryArticle = "SELECT * FROM article WHERE id=1;";
//            ResultSet resultArticle = statement.executeQuery(sqlQueryArticle);
//            resultArticle.next();
//            // сверяем статью
//            soft.assertThat(article)
//                    .hasFieldOrPropertyWithValue("title", resultArticle.getString("title"))
//                    .hasFieldOrPropertyWithValue("lead", resultArticle.getString("lead"))
//                    .hasFieldOrPropertyWithValue("createDate", resultArticle.getTimestamp("create_date"))
//                    .hasFieldOrPropertyWithValue("editDate", resultArticle.getTimestamp("edit_date"))
//                    .hasFieldOrPropertyWithValue("text", resultArticle.getString("text"))
//                    .hasFieldOrPropertyWithValue("isPublished", resultArticle.getBoolean("is_published"))
//                    .hasFieldOrPropertyWithValue("categoryId", resultArticle.getInt("category_id"))
//                    .hasFieldOrPropertyWithValue("userId", resultArticle.getInt("user_id"))
//                    .hasFieldOrPropertyWithValue("sourceId", resultArticle.getInt("source_id"));
//            soft.assertAll();
//            // сверяем изображаения
//            String sqlQueryImages = "SELECT * FROM image WHERE article_id=1;";
//            ResultSet resultImages = statement.executeQuery(sqlQueryImages);
//            resultImages.next();
//            soft.assertThat(articleImage1)
//                    .hasFieldOrPropertyWithValue("title", resultImages.getString("title"))
//                    .hasFieldOrPropertyWithValue("path", resultImages.getString("path"));
//            soft.assertAll();
//            resultImages.next();
//            soft.assertThat(articleImage2)
//                    .hasFieldOrPropertyWithValue("title", resultImages.getString("title"))
//                    .hasFieldOrPropertyWithValue("path", resultImages.getString("path"));
//            soft.assertAll();
//            // сверяем id тегов
//            String sqlQueryIdTags = "SELECT * FROM article_tag WHERE article_id=1;";
//            ResultSet resultIdTags = statement.executeQuery(sqlQueryIdTags);
//            resultIdTags.next();
//            int idTag1 = resultIdTags.getInt("tag_id");
//            resultIdTags.next();
//            int idTag2 = resultIdTags.getInt("tag_id");
//            sqlQueryIdTags = String.format("SELECT * FROM tag WHERE id=%d;", idTag1);
//            ResultSet resultTags = statement.executeQuery(sqlQueryIdTags);
//            resultTags.next();
//            assertThat(tag1).hasFieldOrPropertyWithValue("title", resultTags.getString("title"));
//            sqlQueryIdTags = String.format("SELECT * FROM tag WHERE id=%d;", idTag2);
//            resultTags = statement.executeQuery(sqlQueryIdTags);
//            resultTags.next();
//            assertThat(tag2).hasFieldOrPropertyWithValue("title", resultTags.getString("title"));
//            this.poolConnection.pullConnection(connection);
//        } catch (SQLException exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    @Test
//    void deleteArticle() {
//        try {
//            ArticleRepository articleRepository = new ArticleRepository();
//            Connection connection = this.poolConnection.getConnection();
//            Statement statement = connection.createStatement();
//            // добавляем статью в БД
//            String sqlInsertArticle = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
//                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
//                    "Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
//            statement.executeUpdate(sqlInsertArticle);
//            // добавляем 2 картинки к статье в БД
//            String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
//                            "VALUES ('%s', '%s', %d), ('%s', '%s', %d);",
//                    "Изображение 1", "/static/images/image1.png", 1,
//                    "Изображение 2", "/static/images/image2.png", 1);
//            // добавляем 2 тега к статье в БД
//            statement.executeUpdate(sqlInsertImages);
//            String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
//                    "(1, 1), (1, 2);";
//            statement.executeUpdate(sqlInsertTagsId);
//
//            // выполняем удаление
//            articleRepository.delete(1);
//
//            // делаем запросы в БД и проверяем, удалена ли статья
//            // получаем статью
//            String sqlQueryArticle = "SELECT * FROM article WHERE id=1;";
//            ResultSet result = statement.executeQuery(sqlQueryArticle);
//            assertThat(result.next()).as("Запись класса Article не была удалена").isFalse();
//            // получаем изображения
//            String sqlQueryImages = "SELECT * FROM image WHERE article_id=1;";
//            result = statement.executeQuery(sqlQueryImages);
//            assertThat(result.next()).as("Запись класса Image не была удалена").isFalse();
//            // получаем id тегов
//            String sqlQueryTagsId = "SELECT * FROM article_tag WHERE article_id=1";
//            result = statement.executeQuery(sqlQueryTagsId);
//            assertThat(result.next()).as("Запись из таблицы article_tag не была удалена").isFalse();
//            this.poolConnection.pullConnection(connection);
//        } catch (SQLException exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    @Test
//    void updateArticle() {
//        try {
//            SoftAssertions soft = new SoftAssertions();
//            ArticleRepository articleRepository = new ArticleRepository();
//            Connection connection = this.poolConnection.getConnection();
//            Statement statement = connection.createStatement();
//            // добавляем данные в БД, которые будем обновлять
//            // добавляем статью
//            String sqlInsertArticle = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, " +
//                            "category_id, user_id, source_id) VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s);",
//                    "Заголовок 1", "Лид 1", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1", true, 1, 1, 1);
//            statement.executeUpdate(sqlInsertArticle);
//            // добавляем 2 картинки к статье в БД
//            String sqlInsertImages = String.format("INSERT INTO image (title, path, article_id) " +
//                            "VALUES ('%s', '%s', %d), ('%s', '%s', %d);",
//                    "Изображение 1", "/static/images/image1.png", 1,
//                    "Изображение 2", "/static/images/image2.png", 1);
//            // добавляем 2 тега к статье в БД
//            statement.executeUpdate(sqlInsertImages);
//            String sqlInsertTagsId = "INSERT INTO article_tag (article_id, tag_id) VALUES " +
//                    "(1, 1), (1, 2);";
//            statement.executeUpdate(sqlInsertTagsId);
//            // создаем объект статьи, которым будем обновлять
//            // статья
//            Article article = new Article(1,"Заголовок 1 новый", "Лид 1 новый", Timestamp.valueOf(createDateArticle.atStartOfDay()),
//                    Timestamp.valueOf(editDateArticle.atStartOfDay()), "Текст 1 новый", true, 2, 1, 2);
//            // изображения и теги
//            ArticleImage articleImage1 = new ArticleImage("Изображение 2", "/static/images/image2.png");
//            ArticleImage articleImage2 = new ArticleImage("Изображение 3", "/static/images/image3.png");
//            articleImage1.setArticle(article);
//            articleImage2.setArticle(article);
//            article.addNewImage(articleImage1);
//            article.addNewImage(articleImage2);
//            Tag tag1 = new Tag(2, "update tag");
//            Tag tag2 = new Tag("new tag");
//            tag1.addNewArticle(article);
//            tag2.addNewArticle(article);
//            article.addNewTag(tag1);
//            article.addNewTag(tag2);
//
//            // обновляем данные
//            articleRepository.update(article);
//
//            // получаем данные из БД и сверяем с объектами, которыми обновляли
//            // сверяем статью
//            String sqlQueryArticle = "SELECT * FROM article WHERE id=1;";
//            ResultSet resultArticle = statement.executeQuery(sqlQueryArticle);
//            resultArticle.next();
//            soft.assertThat(article)
//                    .hasFieldOrPropertyWithValue("title", resultArticle.getString("title"))
//                    .hasFieldOrPropertyWithValue("lead", resultArticle.getString("lead"))
//                    .hasFieldOrPropertyWithValue("createDate", resultArticle.getTimestamp("create_date"))
//                    .hasFieldOrPropertyWithValue("editDate", resultArticle.getTimestamp("edit_date"))
//                    .hasFieldOrPropertyWithValue("text", resultArticle.getString("text"))
//                    .hasFieldOrPropertyWithValue("isPublished", resultArticle.getBoolean("is_published"))
//                    .hasFieldOrPropertyWithValue("categoryId", resultArticle.getInt("category_id"))
//                    .hasFieldOrPropertyWithValue("userId", resultArticle.getInt("user_id"))
//                    .hasFieldOrPropertyWithValue("sourceId", resultArticle.getInt("source_id"));
//            soft.assertAll();
//            // сверяем изображения
//            String sqlQueryImages = "SELECT * FROM image WHERE article_id=1 ORDER BY title;";
//            ResultSet resultImages = statement.executeQuery(sqlQueryImages);
//            resultImages.next();
//            soft.assertThat(articleImage1)
//                    .hasFieldOrPropertyWithValue("title", resultImages.getString("title"))
//                    .hasFieldOrPropertyWithValue("path", resultImages.getString("path"));
//            soft.assertAll();
//            resultImages.next();
//            soft.assertThat(articleImage2)
//                    .hasFieldOrPropertyWithValue("title", resultImages.getString("title"))
//                    .hasFieldOrPropertyWithValue("path", resultImages.getString("path"));
//            soft.assertAll();
//            // сверяем id тегов
//            String sqlQueryIdTags = "SELECT * FROM article_tag WHERE article_id=1;";
//            ResultSet resultIdTags = statement.executeQuery(sqlQueryIdTags);
//            resultIdTags.next();
//            int idTag1 = resultIdTags.getInt("tag_id");
//            resultIdTags.next();
//            int idTag2 = resultIdTags.getInt("tag_id");
//            sqlQueryIdTags = String.format("SELECT * FROM tag WHERE id=%d;", idTag1);
//            ResultSet resultTags = statement.executeQuery(sqlQueryIdTags);
//            resultTags.next();
//            assertThat(tag1).hasFieldOrPropertyWithValue("title", resultTags.getString("title"));
//            sqlQueryIdTags = String.format("SELECT * FROM tag WHERE id=%d;", idTag2);
//            resultTags = statement.executeQuery(sqlQueryIdTags);
//            resultTags.next();
//            assertThat(tag2).hasFieldOrPropertyWithValue("title", resultTags.getString("title"));
//            this.poolConnection.pullConnection(connection);
//        } catch (SQLException exception) {
//            exception.printStackTrace();
//        }
//    }
//}
//
//
