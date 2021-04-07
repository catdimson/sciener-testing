package news.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(group.getTitle()).isEqualTo("editor");
    }
}
