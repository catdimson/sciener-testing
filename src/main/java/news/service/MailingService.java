package news.service;

import news.dao.repositories.MailingRepository;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Mailing;

import java.sql.SQLException;
import java.util.List;

public class MailingService implements Service<Mailing> {
    final private MailingRepository mailingRepository;

    public MailingService(MailingRepository mailingRepository) {
        this.mailingRepository = mailingRepository;
    }

    @Override
    public List<Mailing> query(ExtendSqlSpecification<Mailing> specification) throws SQLException {
        return mailingRepository.query(specification);
    }

    @Override
    public void create(Mailing instance) throws SQLException {
        mailingRepository.create(instance);
    }

    @Override
    public void delete(int id) throws SQLException {
        mailingRepository.delete(id);
    }

    @Override
    public void update(Mailing instance) throws SQLException {
        mailingRepository.update(instance);
    }
}
