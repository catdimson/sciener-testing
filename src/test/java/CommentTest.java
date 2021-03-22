import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Date;


class CommentTest {
    private static New article;
    private static User user;
    private static Date createDate;
    private static Date editDate;

    /**
     * Инициализация данных общих для всех тестов
     */
    @BeforeAll
    static void beforeAll() throws NoSuchAlgorithmException {
        // создание пользователя, создавшего новость
        Date lastLogin = new Date();
        Date dateJoined = new Date(16_000_000_000_05L);
        Group group2 = new Group(1, "editor");
        User user2 = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group2, lastLogin, dateJoined, true, true, true);

        // создание новости, для которой писался комментарий
        Date editDateNew = new Date();
        Date createDateNew = new Date(16_000_000_000_05L);
        Category category = new Category(1, "sport");
        article = new New(1, "title 1", "lead 1", createDateNew, editDateNew,
                "description article 1", true, category, user2);

        // создание пользователя, написавшего комментарий
        Group group = new Group(3, "visitor");
        Date lastLoginCommentator = new Date();
        Date dateJoinedCommentator = new Date(16_000_000_000_10L);
        user = new User(2, "qwerty13", "commentator", "mihail", "gorbach",
                "mihagorbach@gmail.com", group, lastLoginCommentator, dateJoinedCommentator, true,
                true, true);

        // данные для создания комментария
        editDate = new Date();
        createDate = new Date(16_000_000_000_00L);
    }

    /**
     * Проверка работы конструктора Comment
     */
    @Test
    void Comment() {
        Comment comment = new Comment(1, "comment 1", createDate, editDate, article, user);
        SoftAssertions soft = new SoftAssertions();

        soft.assertThat(comment)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("text", "comment 1")
                .hasFieldOrPropertyWithValue("createDate", createDate)
                .hasFieldOrPropertyWithValue("editDate", editDate)
                .hasFieldOrPropertyWithValue("article", article)
                .hasFieldOrPropertyWithValue("user", user);
        soft.assertAll();
    }

    /**
     * Проверка метода редактирования изображения
     */
    @Test
    void editComment() {
        Comment comment = new Comment(1, "comment 1", createDate, editDate, article, user);
        SoftAssertions soft = new SoftAssertions();

        comment.edit("comment 2", editDate, article);

        soft.assertThat(comment)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("article", article)
                .hasFieldOrPropertyWithValue("editDate", editDate);
        soft.assertAll();
    }
}