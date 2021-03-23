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
        LocalDate lastLogin = LocalDate.now();
        LocalDate dateJoined = LocalDate.now().minusYears(2);
        Group group2 = new Group(1, "editor");
        User user2 = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group2, lastLogin, dateJoined, true, true, true);

        // создание новости, для которой писался комментарий
        LocalDate editDateNew = LocalDate.now().minusMonths(1);
        LocalDate createDateNew = LocalDate.now().minusMonths(2);
        Category category = new Category(1, "sport");
        article = new New(1, "title 1", "lead 1", createDateNew, editDateNew,
                "description article 1", true, category, user2);

        // создание пользователя, написавшего комментарий
        Group group = new Group(3, "visitor");
        LocalDate lastLoginCommentator = LocalDate.now();
        LocalDate dateJoinedCommentator = LocalDate.now().minusMonths(10);
        user = new User(2, "qwerty13", "commentator", "mihail", "gorbach",
                "mihagorbach@gmail.com", group, lastLoginCommentator, dateJoinedCommentator, true,
                true, true);

        // данные для создания комментария
        editDate = LocalDate.now();
        createDate = LocalDate.now().minusDays(5);
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