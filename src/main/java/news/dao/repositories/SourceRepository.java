package news.dao.repositories;

import news.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceRepository extends JpaRepository<Source, Integer> {

    List<Source> findByTitle(String title);

}
