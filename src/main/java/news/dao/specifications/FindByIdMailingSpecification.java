package news.dao.specifications;

import news.model.Mailing;

public class FindByIdMailingSpecification implements ExtendSqlSpecification<Mailing> {
    final private int id;

    public FindByIdMailingSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Mailing mailing) {
        return mailing.equalsWithId(this.id);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM mailing WHERE id=?;";
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
