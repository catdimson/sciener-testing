import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupTest {

    @Test
    void changeTitle() {
        Group group = new Group(1, "admin");
        group.changeTitle("editor");

        String actual = group.getTitle();
        String expected = "editor";

        assertEquals(expected, actual);
    }
}
