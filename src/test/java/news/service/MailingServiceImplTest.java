package news.service;

import news.dao.repositories.MailingRepository;
import news.model.Mailing;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@DisplayName("Тестирование сервиса для Mailing")
@SpringBootTest
@RunWith(SpringRunner.class)
class MailingServiceImplTest {

    @MockBean
    private MailingRepository mailingRepository;

    @Mock
    private Mailing mailing;

    @Autowired
    private MailingServiceImpl mailingService;

    @DisplayName("Получение всех записей")
    @Test
    void findAll() {
        mailingService.findAll();

        Mockito.verify(mailingRepository, Mockito.times(1)).findAll();
    }

    @DisplayName("Поиск по почте")
    @Test
    void findByTitle() {
        mailingService.findByEmail("test@mail.ru");

        Mockito.verify(mailingRepository, Mockito.times(1)).findByEmail("test@mail.ru");
    }

    @DisplayName("Получение по ID")
    @Test
    void findById() {
        mailingService.findById(1);

        Mockito.verify(mailingRepository, Mockito.times(1)).findById(1);
    }

    @DisplayName("Сохранение сущности")
    @Test
    void createMailing() {
        mailingService.createMailing(mailing);

        Mockito.verify(mailingRepository, Mockito.times(1)).save(mailing);
    }

    @DisplayName("Обновление сущности")
    @Test
    void updateMailing() {
        mailingService.updateMailing(mailing);

        Mockito.verify(mailingRepository, Mockito.times(1)).save(mailing);
    }

    @DisplayName("Удаление сущности")
    @Test
    void deleteMailing() {
        mailingService.deleteMailing(1);

        Mockito.verify(mailingRepository, Mockito.times(1)).deleteById(1);
    }
}