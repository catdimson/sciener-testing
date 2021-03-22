import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void changeTitle() {
        Category category = new Category("sport");
        category.changeTitle("politic");

        String actual = category.getTitle();
        String expected = "politic";

        assertEquals(expected, actual);
    }
}