package news.dao.specifications;

import news.model.User;

public class FindByIdUserSpecification implements SqlSpecification<User> {
    final private int id;

    public FindByIdUserSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(User user) {
        return (int) user.getObjects()[0] == this.id;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM \"user\" WHERE id='%d';", this.id);
    }
}
