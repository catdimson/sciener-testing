package news.dao.repositories;

import news.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    List<Group> findByTitle(String title);

}
