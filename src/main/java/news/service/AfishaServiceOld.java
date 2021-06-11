//package news.service;
//
//import news.dao.repositories.AfishaRepository;
//import news.dao.specifications.ExtendSqlSpecification;
//import news.model.Afisha;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.sql.SQLException;
//import java.util.List;
//
//@org.springframework.stereotype.Service
//public class AfishaServiceOld implements Service<Afisha> {
//    final private AfishaRepository afishaRepository;
//
//    @Autowired
//    public AfishaServiceOld(AfishaRepository afishaRepository) {
//        this.afishaRepository = afishaRepository;
//    }
//
//    @Override
//    public List<Afisha> query(ExtendSqlSpecification<Afisha> specification) throws SQLException {
//        return afishaRepository.query(specification);
//    }
//
//    @Override
//    public int create(Afisha instance) throws SQLException {
//        return afishaRepository.save(instance);
//    }
//
//    @Override
//    public int delete(int id) throws SQLException {
//        return afishaRepository.delete(id);
//    }
//
//    @Override
//    public int update(Afisha instance) throws SQLException {
//        return afishaRepository.update(instance);
//    }
//}
