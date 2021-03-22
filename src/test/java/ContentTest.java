import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContentTest {

    @Test
    void changeEntity() {
        Content content = new Content(1, "Group");
        content.changeEntity("Category");

        String actual = content.getEntity();
        String expected = "Category";

        assertEquals(expected, actual);
    }
}