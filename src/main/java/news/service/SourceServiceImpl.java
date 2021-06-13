package news.service;

import news.dao.repositories.SourceRepository;
import news.model.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class SourceServiceImpl implements SourceService {

    protected SourceRepository sourceRepository;

    public SourceServiceImpl() {}

    @Autowired
    public SourceServiceImpl(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Override
    public List<Source> findAll() {
        return sourceRepository.findAll();
    }

    @Override
    public List<Source> findByTitle(String title) {
        return sourceRepository.findByTitle(title);
    }

    @Override
    public Optional<Source> findById(int id) {
        return sourceRepository.findById(id);
    }

    @Override
    public Source createSource(Source source) {
        return sourceRepository.save(source);
    }

    @Override
    public Source updateSource(Source source) {
        return sourceRepository.save(source);
    }

    @Override
    public void deleteSource(int id) {
        sourceRepository.deleteById(id);
    }
}
