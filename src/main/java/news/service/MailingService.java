package news.service;

import news.model.Mailing;

import java.util.List;
import java.util.Optional;

public interface MailingService {

    List<Mailing> findAll();

    List<Mailing> findByEmail(String title);

    Optional<Mailing> findById(int id);

    Mailing createMailing(Mailing mailing);

    Mailing updateMailing(Mailing mailing);

    void deleteMailing(int id);

}
