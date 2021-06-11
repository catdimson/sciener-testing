package news.service;

import news.model.Afisha;

import java.util.List;
import java.util.Optional;

public interface AfishaService {

    List<Afisha> findAll();

    List<Afisha> findByTitle(String title);

    Optional<Afisha> findById(int id);

    Afisha createAfisha(Afisha afisha);

    Afisha updateAfisha(Afisha afisha);

    void deleteAfisha(int id);

}
