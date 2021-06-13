package news.service;

import news.dao.repositories.CategoryRepository;
import news.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@DisplayName("Тестирование сервиса для Category")
@SpringBootTest
@RunWith(SpringRunner.class)
class CategoryServiceImplTest {

    @MockBean
    private CategoryRepository categoryRepository;

    @Mock
    private Category category;

    @Autowired
    private CategoryServiceImpl categoryService;

    @DisplayName("Получение всех записей")
    @Test
    void findAll() {
        categoryService.findAll();

        Mockito.verify(categoryRepository, Mockito.times(1)).findAll();
    }

    @DisplayName("Поиск по заголовку")
    @Test
    void findByTitle() {
        categoryService.findByTitle("Some_title");

        Mockito.verify(categoryRepository, Mockito.times(1)).findByTitle("Some_title");
    }

    @DisplayName("Получение по ID")
    @Test
    void findById() {
        categoryService.findById(1);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(1);
    }

    @DisplayName("Сохранение сущности")
    @Test
    void createCategory() {
        categoryService.createCategory(category);

        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
    }

    @DisplayName("Обновление сущности")
    @Test
    void updateCategory() {
        categoryService.updateCategory(category);

        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
    }

    @DisplayName("Удаление сущности")
    @Test
    void deleteCategory() {
        categoryService.deleteCategory(1);

        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(1);
    }
}