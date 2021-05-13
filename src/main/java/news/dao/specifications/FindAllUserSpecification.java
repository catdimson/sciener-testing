package news.dao.specifications;

import news.model.User;

public class FindAllUserSpecification implements ExtendSqlSpecification<User> {

    @Override
    public boolean isSpecified(User user) {
        return true;
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM \"user\";";
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
