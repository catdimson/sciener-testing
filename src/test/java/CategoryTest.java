import news.model.Category;
import news.model.Articles;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryTest {

    @Mock
    private final List<Articles> articles = new ArrayList<>();

    @Mock
    private Articles article;

    @Test
    void addNewArticle() {
        Category category = new Category(1, "sport", articles);

        category.addNewArticle(article);

        assertTrue(category.containArticle(article));
    }

    @Test
    void renameTitle() {
        Category category = new Category(1, "sport");
        category.rename("politic");

        String actualTitle = category.getTitle();
        String expectedTitle = "politic";

        assertEquals(expectedTitle, actualTitle);
    }
}