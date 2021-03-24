import news.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    @Test
    void changeTitle() {
        Tag tag = new Tag(1, "it", null);
        tag.changeTitle("ufc");

        String actual = tag.getTitle();
        String expected = "ufc";

        assertEquals(expected, actual);
    }
}