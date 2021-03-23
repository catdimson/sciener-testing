import domain.Group;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupTest {

    @Test
    void changeTitle() {
        Group group = new Group(1, "admin");
        group.changeTitle("editor");

        String actualTitle = group.getTitle();
        String expectedTitle = "editor";

        assertEquals(expectedTitle, actualTitle);
    }
}
