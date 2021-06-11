package news.service;

import news.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    List<User> findByFirstName(String title);

    Optional<User> findById(int id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

}
