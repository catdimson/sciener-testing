import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

class NewTest {
    private static User user;

    private static Category category;

    private static Date editDate;
    private static Date createDate;

    /**
    * Инициализация данных общих для всех тестов
    */
    @BeforeAll
    static void beforeAll() throws NoSuchAlgorithmException {
        Date lastLogin = new Date();
        Date dateJoined = new Date(16_000_000_000_00L);
        Group group = new Group(1, "editor");
        user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true);

        category = new Category(1, "sport");

        editDate = new Date();
        createDate = new Date(16_000_000_000_00L);
    }

    /**
     * Проверка работы конструктора New
     */
    @Test
    void New() {
        New article = new New(1, "title 1", "lead 1", createDate, editDate,
                "description article 1", true, category, user);
        SoftAssertions soft = new SoftAssertions();

        soft.assertThat(article)
            .hasFieldOrPropertyWithValue("id", 1)
            .hasFieldOrPropertyWithValue("title", "title 1")
            .hasFieldOrPropertyWithValue("lead", "lead 1")
            .hasFieldOrPropertyWithValue("createDate", createDate)
            .hasFieldOrPropertyWithValue("editDate", editDate)
            .hasFieldOrPropertyWithValue("text", "description article 1")
            .hasFieldOrPropertyWithValue("isPublished", true)
            .hasFieldOrPropertyWithValue("category", category)
            .hasFieldOrPropertyWithValue("user", user);
        soft.assertAll();
    }

    /**
    * Проверка метода редактирования новости
    */
    @Test
    void editArticle() {
        New article = new New(1, "title 1", "lead 1", createDate, editDate,
                "description article 1", true, category, user);
        SoftAssertions soft = new SoftAssertions();

        article.edit(2, "title 2", "lead 2", editDate,
                "description article 2", false, category, user);

        soft.assertThat(article)
                .hasFieldOrPropertyWithValue("id", 2)
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