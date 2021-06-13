package news.dao.repositories;

import news.model.Mailing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailingRepository extends JpaRepository<Mailing, Integer> {

    List<Mailing> findByEmail(String email);

}
