package news.service;

import news.dao.repositories.AfishaRepository;
import news.model.Afisha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AfishaServiceImpl implements AfishaService {

    protected AfishaRepository afishaRepository;

    public AfishaServiceImpl() {}

    @Autowired
    public AfishaServiceImpl(AfishaRepository afishaRepository) {
        this.afishaRepository = afishaRepository;
    }

    @Override
    public List<Afisha> findAll() {
        return afishaRepository.findAll();
    }

    @Override
    public List<Afisha> findByTitle(String title) {
        return afishaRepository.findByTitle(title);
    }

    @Override
    public Optional<Afisha> findById(int id) {
        return afishaRepository.findById(id);
    }

    @Override
    public Afisha createAfisha(Afisha afisha) {
        return afishaRepository.save(afisha);
    }

    @Override
    public Afisha updateAfisha(Afisha afisha) {
        return afishaRepository.save(afisha);
    }

    @Override
    public void deleteAfisha(int id) {
        afishaRepository.deleteById(id);
    }
}
