package news.service;

import news.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {

    List<Tag> findAll();

    List<Tag> findByTitle(String title);

    Optional<Tag> findById(int id);

    Tag createTag(Tag tag);

    Tag updateTag(Tag tag);

    void deleteTag(int id);

}
