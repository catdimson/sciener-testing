package news.dao.specifications;

import news.model.Afisha;

public class FindAllAfishaSpecification implements ExtendSqlSpecification<Afisha> {
    @Override
    public boolean isSpecified(Afisha afisha) {
        return true;
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM afisha;";
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
