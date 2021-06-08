package news.service;

import news.model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupService {

    List<Group> findAll();

    List<Group> findByTitle(String title);

    Optional<Group> findById(int id);

    Group createGroup(Group group);

    Group updateGroup(Group group);

    void deleteGroup(int id);

}
