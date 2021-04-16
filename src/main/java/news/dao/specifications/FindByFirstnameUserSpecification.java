package news.dao.specifications;

import news.model.User;

public class FindByFirstnameUserSpecification implements SqlSpecification<User> {
    final private String firstName;

    public FindByFirstnameUserSpecification(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public boolean isSpecified(User user) {
        return user.getObjects()[3] == this.firstName;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM \"user\" WHERE first_name='%s';", this.firstName);
    }

    @Override
    public Object getCriterial() {
        return this.firstName;
    }
}
