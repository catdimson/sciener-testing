package news.dao.specifications;

public interface Specification<T> {

    boolean isSpecified(T t);

}
