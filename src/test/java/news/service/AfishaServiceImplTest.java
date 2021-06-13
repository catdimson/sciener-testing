package news.service;

import news.dao.repositories.AfishaRepository;
import news.model.Afisha;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@DisplayName("Тестирование сервиса для Afisha")
@SpringBootTest
@RunWith(SpringRunner.class)
class AfishaServiceImplTest {

    @MockBean
    private AfishaRepository afishaRepository;

    @Mock
    private Afisha afisha;

    @Autowired
    private AfishaServiceImpl afishaService;

    @DisplayName("Получение всех записей")
    @Test
    void findAll() {
        afishaService.findAll();

        Mockito.verify(afishaRepository, Mockito.times(1)).findAll();
    }

    @DisplayName("Поиск по заголовку")
    @Test
    void findByTitle() {
        afishaService.findByTitle("Some_title");

        Mockito.verify(afishaRepository, Mockito.times(1)).findByTitle("Some_title");
    }

    @DisplayName("Получение по ID")
    @Test
    void findById() {
        afishaService.findById(1);

        Mockito.verify(afishaRepository, Mockito.times(1)).findById(1);
    }

    @DisplayName("Сохранение сущности")
    @Test
    void createAfisha() {
        afishaService.createAfisha(afisha);

        Mockito.verify(afishaRepository, Mockito.times(1)).save(afisha);
    }

    @DisplayName("Обновление сущности")
    @Test
    void updateAfisha() {
        afishaService.updateAfisha(afisha);

        Mockito.verify(afishaRepository, Mockito.times(1)).save(afisha);
    }

    @DisplayName("Удаление сущности")
    @Test
    void deleteAfisha() {
        afishaService.deleteAfisha(1);

        Mockito.verify(afishaRepository, Mockito.times(1)).deleteById(1);
    }
}