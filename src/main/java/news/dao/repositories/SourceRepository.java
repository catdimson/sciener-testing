package news.dao.repositories;

import news.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Integer> {

    List<Source> findByTitle(String title);

}
