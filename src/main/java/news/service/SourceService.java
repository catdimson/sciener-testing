package news.service;

import news.model.Source;

import java.util.List;
import java.util.Optional;

public interface SourceService {

    List<Source> findAll();

    List<Source> findByTitle(String title);

    Optional<Source> findById(int id);

    Source createSource(Source source);

    Source updateSource(Source source);

    void deleteSource(int id);

}
