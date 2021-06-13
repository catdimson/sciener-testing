package news.dao.repositories;

import news.model.Afisha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AfishaRepository extends JpaRepository<Afisha, Integer> {

    List<Afisha> findByTitle(String title);

}
