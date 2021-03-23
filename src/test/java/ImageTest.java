import news.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

class ImageTest {
    private static New article;

    /**
     * Инициализация данных общих для всех тестов
     */
    @BeforeAll
    static void beforeAll() throws NoSuchAlgorithmException {
        LocalDate lastLogin = LocalDate.now();
        LocalDate dateJoined = LocalDate.now().minusMonths(5);
        Group group = new Group(1, "editor");
        User user = new User(1, "qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", group, lastLogin, dateJoined, true, true, true);

        LocalDate editDate = LocalDate.now();
        LocalDate createDate = LocalDate.now().plusDays(10);
        Category category = new Category(1, "sport");

        article = new New(1, "title 1", "lead 1", createDate, editDate,
                "description article 1", true, category, user);

    }

    /**
     * Проверка метода редактирования изображения
     */
    @Test
    void editImage() {
        Image image = new Image(1, "image 1", "/static/news/", article);
        SoftAssertions soft = new SoftAssertions();

        image.edit("image 2", "/static/news/2", article);

        soft.assertThat(image)
                .hasFieldOrPropertyWithValue("title", "image 2")
                .hasFieldOrPropertyWithValue("path", "/static/news/2")
                .hasFieldOrPropertyWithValue("article", article);
        soft.assertAll();
    }
}