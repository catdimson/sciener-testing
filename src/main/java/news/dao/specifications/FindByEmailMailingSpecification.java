package news.dao.specifications;

import news.model.Mailing;

public class FindByEmailMailingSpecification implements ExtendSqlSpecification<Mailing> {
    final private String email;

    public FindByEmailMailingSpecification(String email) {
        this.email = email;
    }

    @Override
    public boolean isSpecified(Mailing mailing) {
        return mailing.equalsWithEmail(this.email);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM mailing WHERE email=?;";
    }

    @Override
    public Object getCriterial() {
        return this.email;
    }

    @Override
    public boolean isById() {
        return false;
    }
}
