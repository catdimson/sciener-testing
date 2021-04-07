package news.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тестирование тега (Tag)
 */
class TagTest {

    /**
     * Переиманование заголовка тега
     */
    @Test
    void changeTitle() {
        Tag tag = new Tag(1, "it");

        tag.rename("ufc");

        assertThat(tag.getTitle()).isEqualTo("ufc");
    }
}