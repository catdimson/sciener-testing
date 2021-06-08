package news.dao.repositories;

import news.model.Mailing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailingRepository extends JpaRepository<Mailing, Integer> {

    List<Mailing> findByEmail(String email);

}
