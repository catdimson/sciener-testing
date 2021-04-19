package news.dao.specifications;

import news.model.User;

public class FindByFirstnameUserSpecification implements ExtendSqlSpecification<User> {
    final private String firstName;

    public FindByFirstnameUserSpecification(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public boolean isSpecified(User user) {
        return user.equalsWithFirstname(this.firstName);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM \"user\" WHERE first_name=?;";
    }

    @Override
    public Object getCriterial() {
        return this.firstName;
    }

    @Override
    public boolean isById() {
        return false;
    }
}
