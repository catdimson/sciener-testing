import news.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

class CommentTest {
    private static New article;
    private static User user;
    private static LocalDate createDate;
    private static LocalDate editDate;

    /**
     * Инициализация данных общих для всех тестов
     */
    @BeforeAll
    static void beforeAll() throws NoSuchAlgorithmException {
        // создание пользователя, создавшего новость
        LocalDate lastLogin = LocalDate.of(2018, 5, 20);
        LocalDate dateJoined = LocalDate.of(2020, 3, 10);
        Group group2 = new Group(1, "editor", null, null);
        User user2 = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group2, lastLogin, dateJoined, true, true, true,
                null, null, null);

        // создание новости, для которой писался комментарий
        LocalDate editDateNew = LocalDate.of(2019, 10, 15);
        LocalDate createDateNew = LocalDate.of(2019, 10, 15);
        Category category = new Category(1, "sport", null);
        article = new New(1, "title 1", "lead 1", createDateNew, editDateNew,
                "description article 1", true, category, user2, null, null, null);

        // создание пользователя, написавшего комментарий
        Group group = new Group(3, "visitor", null, null);
        LocalDate lastLoginCommentator = LocalDate.of(2020, 3, 15);
        LocalDate dateJoinedCommentator = LocalDate.of(2020, 4, 20);
        user = new User(2, "qwerty13", "commentator", "mihail", "gorbach",
                "mihagorbach@gmail.com", group, lastLoginCommentator, dateJoinedCommentator, true,
                true, true, null, null, null);

        // данные для создания комментария
        editDate = LocalDate.of(2020, 4, 20);
        createDate = LocalDate.of(2020, 4, 20);
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
                .hasFieldOrPropertyWithValue("text", "comment 2")
                .hasFieldOrPropertyWithValue("article", article)
                .hasFieldOrPropertyWithValue("editDate", editDate);
        soft.assertAll();
    }
}