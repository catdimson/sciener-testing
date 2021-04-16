package news.dao.specifications;

import news.model.Mailing;

public class FindByIdMailingSpecification implements SqlSpecification<Mailing> {
    final private int id;

    public FindByIdMailingSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Mailing mailing) {
        return (int) mailing.getObjects()[0] == this.id;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM mailing WHERE id='%d';", this.id);
    }

    @Override
    public Object getCriterial() {
        return this.id;
    }
}
