package news.service;

import news.dao.repositories.SourceRepository;
import news.model.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@DisplayName("Тестирование сервиса для Source")
@SpringBootTest
@RunWith(SpringRunner.class)
class SourceServiceImplTest {

    @MockBean
    private SourceRepository sourceRepository;

    @Mock
    private Source source;

    @Autowired
    private SourceServiceImpl sourceService;

    @DisplayName("Получение всех записей")
    @Test
    void findAll() {
        sourceService.findAll();

        Mockito.verify(sourceRepository, Mockito.times(1)).findAll();
    }

    @DisplayName("Поиск по заголовку")
    @Test
    void findByTitle() {
        sourceService.findByTitle("Some_title");

        Mockito.verify(sourceRepository, Mockito.times(1)).findByTitle("Some_title");
    }

    @DisplayName("Получение по ID")
    @Test
    void findById() {
        sourceService.findById(1);

        Mockito.verify(sourceRepository, Mockito.times(1)).findById(1);
    }

    @DisplayName("Сохранение сущности")
    @Test
    void createSource() {
        sourceService.createSource(source);

        Mockito.verify(sourceRepository, Mockito.times(1)).save(source);
    }

    @DisplayName("Обновление сущности")
    @Test
    void updateSource() {
        sourceService.updateSource(source);

        Mockito.verify(sourceRepository, Mockito.times(1)).save(source);
    }

    @DisplayName("Удаление сущности")
    @Test
    void deleteSource() {
        sourceService.deleteSource(1);

        Mockito.verify(sourceRepository, Mockito.times(1)).deleteById(1);
    }
}