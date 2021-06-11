package news.dao.repositories;

import news.model.Afisha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AfishaRepository extends JpaRepository<Afisha, Integer> {

    List<Afisha> findByTitle(String title);

}
