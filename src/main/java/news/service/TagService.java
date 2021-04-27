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
    public void create(Tag instance) throws SQLException {
        tagRepository.create(instance);
    }

    @Override
    public void delete(int id) throws SQLException {
        tagRepository.delete(id);
    }

    @Override
    public void update(Tag instance) throws SQLException {
        tagRepository.update(instance);
    }
}
