package news.service;

import news.dao.repositories.UserRepository;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService implements Service<User> {
    final private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> query(ExtendSqlSpecification<User> specification) throws SQLException {
        return userRepository.query(specification);
    }

    @Override
    public int create(User instance) throws SQLException {
        return userRepository.create(instance);
    }

    @Override
    public void delete(int id) throws SQLException {
        userRepository.delete(id);
    }

    @Override
    public void update(User instance) throws SQLException {
        userRepository.update(instance);
    }
}
