package news.service;

import news.dao.repositories.TagRepository;
import news.model.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@DisplayName("Тестирование сервиса для Tag")
@SpringBootTest
@RunWith(SpringRunner.class)
class TagServiceImplTest {

    @MockBean
    private TagRepository tagRepository;

    @Mock
    private Tag tag;

    @Autowired
    private TagServiceImpl tagService;

    @DisplayName("Получение всех записей")
    @Test
    void findAll() {
        tagService.findAll();

        Mockito.verify(tagRepository, Mockito.times(1)).findAll();
    }

    @DisplayName("Поиск по заголовку")
    @Test
    void findByTitle() {
        tagService.findByTitle("Some_title");

        Mockito.verify(tagRepository, Mockito.times(1)).findByTitle("Some_title");
    }

    @DisplayName("Получение по ID")
    @Test
    void findById() {
        tagService.findById(1);

        Mockito.verify(tagRepository, Mockito.times(1)).findById(1);
    }

    @DisplayName("Сохранение сущности")
    @Test
    void createTag() {
        tagService.createTag(tag);

        Mockito.verify(tagRepository, Mockito.times(1)).save(tag);
    }

    @DisplayName("Обновление сущности")
    @Test
    void updateTag() {
        tagService.updateTag(tag);

        Mockito.verify(tagRepository, Mockito.times(1)).save(tag);
    }

    @DisplayName("Удаление сущности")
    @Test
    void deleteTag() {
        tagService.deleteTag(1);

        Mockito.verify(tagRepository, Mockito.times(1)).deleteById(1);
    }
}