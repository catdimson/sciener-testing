package news.service;

import news.dao.repositories.TagRepository;
import news.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    protected TagRepository tagRepository;

    public TagServiceImpl() {}

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> findByTitle(String title) {
        return tagRepository.findByTitle(title);
    }

    @Override
    public Optional<Tag> findById(int id) {
        return tagRepository.findById(id);
    }

    @Override
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public void deleteTag(int id) {
        tagRepository.deleteById(id);
    }
}
