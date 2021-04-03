package news.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Тестирование класса изображения !!!!!!!!!!!!!!!!!
 * */
class ImageTest {

    @Mock
    private Article article;

    /**
     * Редактирования изображения
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