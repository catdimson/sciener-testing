package news.dao.specifications;

import news.model.Mailing;

public class FindAllMailingSpecification implements ExtendSqlSpecification<Mailing> {

    @Override
    public boolean isSpecified(Mailing mailing) {
        return true;
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM mailing;";
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
