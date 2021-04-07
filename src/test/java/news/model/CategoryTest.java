package news.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тестирование категории (Category)
 */
class CategoryTest {

    /**
     * Сменить название категории
     */
    @Test
    void renameTitle() {
        Category category = new Category("sport");

        category.rename("politic");

        assertThat(category.getTitle()).isEqualTo("politic");
    }
}