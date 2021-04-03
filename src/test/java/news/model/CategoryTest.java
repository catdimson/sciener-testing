package news.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryTest {

    @Test
    void renameTitle() {
        Category category = new Category("sport");
        category.rename("politic");

        String actualTitle = category.getTitle();
        String expectedTitle = "politic";

        assertEquals(expectedTitle, actualTitle);
    }
}