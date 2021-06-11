package news.dao.repositories;

import news.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Integer> {

    List<Group> findByTitle(String title);

}
