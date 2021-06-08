package news.service;

import news.dao.repositories.MailingRepository;
import news.model.Mailing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MailingServiceImpl implements MailingService {

    protected MailingRepository mailingRepository;

    public MailingServiceImpl() {}

    @Autowired
    public MailingServiceImpl(MailingRepository mailingRepository) {
        this.mailingRepository = mailingRepository;
    }

    @Override
    public List<Mailing> findAll() {
        return mailingRepository.findAll();
    }

    @Override
    public List<Mailing> findByEmail(String email) {
        return mailingRepository.findByEmail(email);
    }

    @Override
    public Optional<Mailing> findById(int id) {
        return mailingRepository.findById(id);
    }

    @Override
    public Mailing createMailing(Mailing mailing) {
        return mailingRepository.save(mailing);
    }

    @Override
    public Mailing updateMailing(Mailing mailing) {
        return mailingRepository.save(mailing);
    }

    @Override
    public void deleteMailing(int id) {
        mailingRepository.deleteById(id);
    }
}
