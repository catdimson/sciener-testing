package news.dao.specifications;

import news.model.User;

public class FindByIdUserSpecification implements ExtendSqlSpecification<User> {
    final private int id;

    public FindByIdUserSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(User user) {
        return user.equalsWithId(this.id);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM \"user\" WHERE id=?;";
    }

    @Override
    public Object getCriterial() {
        return this.id;
    }

    @Override
    public boolean isById() {
        return true;
    }
}
