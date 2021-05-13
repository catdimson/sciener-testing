package news.dao.specifications;

import news.model.Source;

public class FindAllSourceSpecification implements ExtendSqlSpecification<Source> {

    @Override
    public boolean isSpecified(Source source) {
        return true;
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM source;";
    }

    @Override
    public Object getCriterial() {
        return null;
    }

    @Override
    public boolean isById() {
        return false;
    }
}
