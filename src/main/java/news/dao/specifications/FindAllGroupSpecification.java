package news.dao.specifications;

import news.model.Group;

public class FindAllGroupSpecification implements ExtendSqlSpecification<Group> {

    @Override
    public boolean isSpecified(Group group) {
        return true;
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM \"group\";";
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
