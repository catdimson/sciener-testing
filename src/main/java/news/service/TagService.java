package news.service;

import news.dao.repositories.TagRepository;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Tag;

import java.sql.SQLException;
import java.util.List;

public class TagService implements Service<Tag> {
    final private TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> query(ExtendSqlSpecification<Tag> specification) throws SQLException {
        return tagRepository.query(specification);
    }

    @Override
    public int create(Tag instance) throws SQLException {
        return tagRepository.create(instance);
    }

    @Override
    public int delete(int id) throws SQLException {
        return tagRepository.delete(id);
    }

    @Override
    public int update(Tag instance) throws SQLException {
        return tagRepository.update(instance);
    }
}