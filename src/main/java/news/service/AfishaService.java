package news.service;

import news.dao.repositories.AfishaRepository;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Afisha;

import java.sql.SQLException;
import java.util.List;

public class AfishaService implements Service<Afisha> {
    final private AfishaRepository afishaRepository;

    public AfishaService(AfishaRepository afishaRepository) {
        this.afishaRepository = afishaRepository;
    }

    @Override
    public List<Afisha> query(ExtendSqlSpecification<Afisha> specification) throws SQLException {
        return afishaRepository.query(specification);
    }

    @Override
    public int create(Afisha instance) throws SQLException {
        return afishaRepository.create(instance);
    }

    @Override
    public int delete(int id) throws SQLException {
        return afishaRepository.delete(id);
    }

    @Override
    public int update(Afisha instance) throws SQLException {
        return afishaRepository.update(instance);
    }
}
