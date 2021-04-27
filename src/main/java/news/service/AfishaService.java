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
    public void create(Afisha instance) throws SQLException {
        afishaRepository.create(instance);
    }

    @Override
    public void delete(int id) throws SQLException {
        afishaRepository.delete(id);
    }

    @Override
    public void update(Afisha instance) throws SQLException {
        afishaRepository.update(instance);
    }
}
