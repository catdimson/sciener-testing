package news.dao.specifications;

import news.model.Mailing;

public class FindByEmailMailingSpecification implements SqlSpecification<Mailing> {
    final private String email;

    public FindByEmailMailingSpecification(String email) {
        this.email = email;
    }

    @Override
    public boolean isSpecified(Mailing mailing) {
        return mailing.getObjects()[1] == this.email;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM mailing WHERE email='%s';", this.email);
    }

    @Override
    public Object getCriterial() {
        return this.email;
    }
}
