import news.Category;
import news.Group;
import news.New;
import news.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

class NewTest {
    private static User user;
    private static Category category;
    private static LocalDate editDate;
    private static LocalDate createDate;

    /**
     * Инициализация данных общих для всех тестов
     */
    @BeforeAll
    static void beforeAll() throws NoSuchAlgorithmException {
        LocalDate lastLogin = LocalDate.of(2019, 5, 20);
        LocalDate dateJoined = LocalDate.of(2020, 5, 20);
        Group group = new Group(1, "editor", null, null);
        user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true,
                null, null, null);

        category = new Category(1, "sport", null);

        editDate = LocalDate.of(2020, 5, 20);
        createDate = LocalDate.of(2020, 5, 20);
    }

    /**
     * Проверка метода редактирования новости
     */
    @Test
    void editArticle() {
        New article = new New(1, "title 1", "lead 1", createDate, editDate, "description article 1",
                true, category, user, null, null, null);
        SoftAssertions soft = new SoftAssertions();

        article.edit("title 2", "lead 2", editDate,
                "description article 2", false, category, user);

        soft.assertThat(article)
                .hasFieldOrPropertyWithValue("title", "title 2")
                .hasFieldOrPropertyWithValue("lead", "lead 2")
                .hasFieldOrPropertyWithValue("editDate", editDate)
                .hasFieldOrPropertyWithValue("text", "description article 2")
                .hasFieldOrPropertyWithValue("isPublished", false)
                .hasFieldOrPropertyWithValue("category", category)
                .hasFieldOrPropertyWithValue("user", user);
        soft.assertAll();
    }
}