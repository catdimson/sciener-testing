package news.dao.specifications;

public interface ExtendSqlSpecification<T> extends SqlSpecification<T> {

    boolean isById();

    Object getCriterial();

}