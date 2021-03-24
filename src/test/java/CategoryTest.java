import news.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void changeTitle() {

        Category category = new Category(1, "sport", null);
        category.changeTitle("politic");

        String actualTitle = category.getTitle();
        String expectedTitle = "politic";

        assertEquals(expectedTitle, actualTitle);
    }
}