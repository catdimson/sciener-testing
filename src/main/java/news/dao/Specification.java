package news.dao;

public interface Specification<T> {

    boolean isSpecified(T t);

}
