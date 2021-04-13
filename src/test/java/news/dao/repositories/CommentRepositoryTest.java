package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.FindByIdCommentSpecification;
import news.dao.specifications.FindByUserIdCommentSpecification;
import news.model.Comment;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class CommentRepositoryTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;
    private static LocalDate createDateComment;
    private static LocalDate editDateComment;
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
        // comment (дата создания, дата редактировани)
        createDateComment = LocalDate.of(2019, 5, 20);
        editDateComment = LocalDate.of(2020, 5, 20);
        commentUserId = 1;
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

        // осздание группы
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
        String sqlCreateSource = "INSERT INTO source (title, url) VALUES('Яндекс ДЗЕН', 'https://zen.yandex.ru/');";
        statement.executeUpdate(sqlCreateSource);

        // создание категории
        String sqlCreateTableCategory = "CREATE TABLE IF NOT EXISTS category (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ), " +
                "title character varying(50) NOT NULL, " +
                "CONSTRAINT category_pk PRIMARY KEY (id)," +
                "CONSTRAINT title_unique_category UNIQUE (title));";
        statement.executeUpdate(sqlCreateTableCategory);
        String sqlCreateCategory = "INSERT INTO category (title) VALUES('Спорт');";
        statement.executeUpdate(sqlCreateCategory);

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
        String sqlCreateArticle = String.format("INSERT INTO article (title, lead, create_date, edit_date, text, is_published, category_id, user_id, source_id) " +
                "VALUES('title1', 'lead1', '%s', '%s', 'text1', true, 1, %s, 1);", Timestamp.valueOf(createDateArticle.atStartOfDay()),
                Timestamp.valueOf(editDateArticle.atStartOfDay()), articleUserId);
        statement.executeUpdate(sqlCreateArticle);

        // СОЗДАНИЕ ТАБЛИЦЫ КОММЕНТАРИЕВ
        String sqlCreateTableComment = "CREATE TABLE IF NOT EXISTS comment (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),\n" +
                "text character varying(3000)," +
                "create_date timestamp NOT NULL," +
                "edit_date timestamp NOT NULL," +
                "article_id integer NOT NULL," +
                "user_id integer NOT NULL," +
                "CONSTRAINT comment_pk PRIMARY KEY (id)," +
                "CONSTRAINT fk_new FOREIGN KEY (article_id)" +
                "    REFERENCES article (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE CASCADE," +
                "CONSTRAINT fk_user FOREIGN KEY (user_id)" +
                "    REFERENCES \"user\" (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE CASCADE);" +
                "CREATE INDEX IF NOT EXISTS fk_index_comment_new_id ON comment (article_id);" +
                "CREATE INDEX IF NOT EXISTS fk_index_comment_user_id ON comment (user_id);";
        statement.executeUpdate(sqlCreateTableComment);

        // СОЗДАНИЕ ТАБЛИЦЫ ПРИКРЕПЛЕНИЙ К КОММЕНТАРИЯМ
        String sqlCreateTableAttachment = "CREATE TABLE IF NOT EXISTS attachment (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "title character varying(80) NOT NULL," +
                "path character varying(500) NOT NULL," +
                "comment_id integer NOT NULL," +
                "CONSTRAINT attachment_pk PRIMARY KEY (id)," +
                "CONSTRAINT fk_comment FOREIGN KEY (comment_id)" +
                "    REFERENCES comment (id) MATCH SIMPLE" +
                "    ON UPDATE CASCADE" +
                "    ON DELETE CASCADE);" +
                "CREATE INDEX IF NOT EXISTS fk_index_attachment_comment_id ON attachment (comment_id);";
        statement.executeUpdate(sqlCreateTableAttachment);
    }

    @Test
    void findById() {
        try {
            SoftAssertions soft = new SoftAssertions();
            CommentRepository commentRepository = new CommentRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            Comment comment = new Comment("Текст комментария", createDateComment, editDateComment, 1, 1);
            Comment.CommentAttachment commentAttachment1 = new Comment.CommentAttachment("Прикрепление 1", "/static/attachments/image1.png", 1);
            Comment.CommentAttachment commentAttachment2 = new Comment.CommentAttachment("Прикрепление 2", "/static/attachments/image2.png", 1);
            comment.addNewAttachment(commentAttachment1);
            comment.addNewAttachment(commentAttachment2);
            String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                    "VALUES ('%s', '%s', '%s', %s, %s);", "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()),
                    Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1);
            statement.executeUpdate(sqlInsertComment);
            String sqlInsertAttachments = String.format("INSERT INTO attachment (title, path, comment_id)" +
                    "VALUES ('%s', '%s', %s), ('%s', '%s', %s);", "Прикрепление 1", "/static/attachments/image1.png", 1,
                    "Прикрепление 2", "/static/attachments/image2.png", 1);
            statement.executeUpdate(sqlInsertAttachments);

            FindByIdCommentSpecification findById = new FindByIdCommentSpecification(1);
            List<Comment> resultFindByIdCommentList = commentRepository.query(findById);
            Object[] resultFindByIdCommentInstance = resultFindByIdCommentList.get(0).getObjects();
            ArrayList attachments = (ArrayList) resultFindByIdCommentInstance[6];
            Comment.CommentAttachment resultFindByIdAttachment1 = (Comment.CommentAttachment) attachments.get(0);
            Comment.CommentAttachment resultFindByIdAttachment2 = (Comment.CommentAttachment) attachments.get(1);

            soft.assertThat(comment)
                    .hasFieldOrPropertyWithValue("text", resultFindByIdCommentInstance[1])
                    .hasFieldOrPropertyWithValue("createDate", resultFindByIdCommentInstance[2])
                    .hasFieldOrPropertyWithValue("editDate", resultFindByIdCommentInstance[3])
                    .hasFieldOrPropertyWithValue("articleId", resultFindByIdCommentInstance[4])
                    .hasFieldOrPropertyWithValue("userId", resultFindByIdCommentInstance[5]);
            soft.assertAll();
            soft.assertThat(resultFindByIdAttachment1)
                    .hasFieldOrPropertyWithValue("title", "Прикрепление 1")
                    .hasFieldOrPropertyWithValue("path", "/static/attachments/image1.png")
                    .hasFieldOrPropertyWithValue("commentId", 1);
            soft.assertAll();
            soft.assertThat(resultFindByIdAttachment2)
                    .hasFieldOrPropertyWithValue("title", "Прикрепление 2")
                    .hasFieldOrPropertyWithValue("path", "/static/attachments/image2.png")
                    .hasFieldOrPropertyWithValue("commentId", 1);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void findByUserId() {
        try {
            SoftAssertions soft = new SoftAssertions();
            CommentRepository commentRepository = new CommentRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            // комментарий пользователя с id=1
            Comment comment = new Comment("Текст комментария", createDateComment, editDateComment, 1, 1);
            // 2 прикрепления к комментарию выше
            Comment.CommentAttachment commentAttachment1 = new Comment.CommentAttachment("Прикрепление 1", "/static/attachments/image1.png", 1);
            Comment.CommentAttachment commentAttachment2 = new Comment.CommentAttachment("Прикрепление 2", "/static/attachments/image2.png", 1);
            comment.addNewAttachment(commentAttachment1);
            comment.addNewAttachment(commentAttachment2);
            // добавляем 2 комментария в БД
            String sqlInsertComment = String.format("INSERT INTO comment (text, create_date, edit_date, article_id, user_id) " +
                            "VALUES ('%s', '%s', '%s', %s, %s), ('%s', '%s', '%s', %s, %s);",
                    "Текст комментария", Timestamp.valueOf(createDateComment.atStartOfDay()), Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 1,
                    "Текст комментария 2", Timestamp.valueOf(createDateComment.atStartOfDay()), Timestamp.valueOf(editDateComment.atStartOfDay()), 1, 2);
            statement.executeUpdate(sqlInsertComment);
            // добавляем 2 прикрепления для комментария 2 в БД
            String sqlInsertAttachmentsComment2 = String.format("INSERT INTO attachment (title, path, comment_id) " +
                            "VALUES ('%s', '%s', %s), ('%s', '%s', %s);",
                    "Прикрепление 3", "/static/attachments/image3.png", 2,
                    "Прикрепление 4", "/static/attachments/image4.png", 2);
            statement.executeUpdate(sqlInsertAttachmentsComment2);
            // добавляем 2 прикрепления для комментария 1 в БД
            String sqlInsertAttachmentsComment1 = String.format("INSERT INTO attachment (title, path, comment_id) " +
                    "VALUES ('%s', '%s', %s), ('%s', '%s', %s);",
                    "Прикрепление 1", "/static/attachments/image1.png", 1,
                    "Прикрепление 2", "/static/attachments/image2.png", 1);
            statement.executeUpdate(sqlInsertAttachmentsComment1);

            FindByUserIdCommentSpecification findByUserId = new FindByUserIdCommentSpecification(1);
            List<Comment> resultFindByUserIdCommentList = commentRepository.query(findByUserId);
            Comment testComment = resultFindByUserIdCommentList.get(0);
            Object[] resultFindByUserIdCommentInstance = resultFindByUserIdCommentList.get(0).getObjects();
            ArrayList attachments = (ArrayList) resultFindByUserIdCommentInstance[6];
            Comment.CommentAttachment resultFindByUserIdAttachment1 = (Comment.CommentAttachment) attachments.get(0);
            Comment.CommentAttachment resultFindByUserIdAttachment2 = (Comment.CommentAttachment) attachments.get(1);

            soft.assertThat(comment)
                    .hasFieldOrPropertyWithValue("text", resultFindByUserIdCommentInstance[1])
                    .hasFieldOrPropertyWithValue("createDate", resultFindByUserIdCommentInstance[2])
                    .hasFieldOrPropertyWithValue("editDate", resultFindByUserIdCommentInstance[3])
                    .hasFieldOrPropertyWithValue("articleId", resultFindByUserIdCommentInstance[4])
                    .hasFieldOrPropertyWithValue("userId", resultFindByUserIdCommentInstance[5]);
            soft.assertAll();
            soft.assertThat(resultFindByUserIdAttachment1)
                    .hasFieldOrPropertyWithValue("title", "Прикрепление 1")
                    .hasFieldOrPropertyWithValue("path", "/static/attachments/image1.png")
                    .hasFieldOrPropertyWithValue("commentId", 1);
            soft.assertAll();
            soft.assertThat(resultFindByUserIdAttachment2)
                    .hasFieldOrPropertyWithValue("title", "Прикрепление 2")
                    .hasFieldOrPropertyWithValue("path", "/static/attachments/image2.png")
                    .hasFieldOrPropertyWithValue("commentId", 1);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void createComment() {
        try {
            SoftAssertions soft = new SoftAssertions();
            CommentRepository commentRepository = new CommentRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            Comment comment = new Comment("Текст комментария", createDateComment, editDateComment, 1, 1);
            Comment.CommentAttachment commentAttachment1 = new Comment.CommentAttachment("Прикрепление 1", "/static/attachments/image1.png", 1);
            Comment.CommentAttachment commentAttachment2 = new Comment.CommentAttachment("Прикрепление 2", "/static/attachments/image2.png", 1);
            comment.addNewAttachment(commentAttachment1);
            comment.addNewAttachment(commentAttachment2);

            commentRepository.create(comment);

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
    }

    /*@Test
    void deleteAfisha() {
        try {
            AfishaRepository afishaRepository = new AfishaRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlCreateAfisha = String.format("INSERT INTO afisha" +
                            "(title, image_url, lead, description, age_limit, timing, place, phone, date, is_commercial, user_id, source_id) " +
                            "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %d, %d);",
                    "Масленица", "/media/maslenica.jpg", "Празничные гуляния на площади", "Описание масленичных гуляний", "0", "180", "Центральная площадь, г.Белгород",
                    "89202005544", Timestamp.valueOf(date.atStartOfDay()), false, 1, 1);
            statement.executeUpdate(sqlCreateAfisha, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            afishaRepository.delete(generatedKeys.getInt(1));

            String sqlQueryInstance = String.format("SELECT * FROM afisha WHERE id=%d;", generatedKeys.getInt(1));
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            assertThat(result.next()).as("Запись класса Afisha не была удалена").isFalse();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }*/

    /*@Test
    void updateUser() {
        try {
            SoftAssertions soft = new SoftAssertions();
            AfishaRepository afishaRepository = new AfishaRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlCreateAfisha = String.format("INSERT INTO afisha" +
                            "(title, image_url, lead, description, age_limit, timing, place, phone, date, is_commercial, user_id, source_id) " +
                            "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %d, %d);",
                    "Масленица", "/media/maslenica.jpg", "Празничные гуляния на площади", "Описание масленичных гуляний", "0", "180", "Центральная площадь, г.Белгород",
                    "89202005544", Timestamp.valueOf(date.atStartOfDay()), false, 1, 1);
            statement.executeUpdate(sqlCreateAfisha, Statement.RETURN_GENERATED_KEYS);
            Afisha afisha2 = new Afisha(1, "Масленица. Конкурсы.", "/media/maslenicaprazdnik.jpg", "Конкурсы на празник", "Красивое описание масленичных конкурсов",
                    "3", "120", "Кинотеатр русич", "89208880022", date, false, 1, 1);

            afishaRepository.update(afisha2);

            String sqlQueryInstance = String.format("SELECT * FROM afisha WHERE id=%d;", 1);
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            result.next();
            soft.assertThat(afisha2)
                    .hasFieldOrPropertyWithValue("title", result.getString(2))
                    .hasFieldOrPropertyWithValue("imageUrl", result.getString(3))
                    .hasFieldOrPropertyWithValue("lead", result.getString(4))
                    .hasFieldOrPropertyWithValue("description", result.getString(5))
                    .hasFieldOrPropertyWithValue("ageLimit", result.getString(6))
                    .hasFieldOrPropertyWithValue("timing", result.getString(7))
                    .hasFieldOrPropertyWithValue("place", result.getString(8))
                    .hasFieldOrPropertyWithValue("phone", result.getString(9))
                    .hasFieldOrPropertyWithValue("date", result.getTimestamp(10).toLocalDateTime().toLocalDate())
                    .hasFieldOrPropertyWithValue("isCommercial", result.getBoolean(11))
                    .hasFieldOrPropertyWithValue("userId", result.getInt(12))
                    .hasFieldOrPropertyWithValue("sourceId", result.getInt(13));
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }*/
}