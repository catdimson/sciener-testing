package news.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тестированание группы (Group)
 * */
public class GroupTest {

    /**
     * Переиманования группы
     * */
    @Test
    void changeTitle() {
        Group group = new Group("admin");
        group.rename("editor");

        String actualTitle = group.getTitle();
        String expectedTitle = "editor";

        assertEquals(expectedTitle, actualTitle);
    }
}
