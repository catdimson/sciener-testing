package news.service;

import news.dao.repositories.SourceRepository;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Source;

import java.sql.SQLException;
import java.util.List;

public class SourceService implements Service<Source> {
    final private SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Override
    public List<Source> query(ExtendSqlSpecification<Source> specification) throws SQLException {
        return sourceRepository.query(specification);
    }

    @Override
    public int create(Source instance) throws SQLException {
        return sourceRepository.create(instance);
    }

    @Override
    public int delete(int id) throws SQLException {
        return sourceRepository.delete(id);
    }

    @Override
    public int update(Source instance) throws SQLException {
        return sourceRepository.update(instance);
    }
}
