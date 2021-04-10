package news.dao.specifications;

public interface SqlSpecification<T> extends Specification<T> {

    String toSqlClauses();

}
