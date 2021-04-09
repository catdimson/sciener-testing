package news.dao;

import java.util.List;

public interface Repository<T> {

    List<T> query(Specification<T> specification);

    void create(T instance);
    void delete(int id);
    void update(T instance);

}
