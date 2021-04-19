package news.dao.specifications;

import news.model.Afisha;

public class FindByIdAfishaSpecification implements ExtendSqlSpecification<Afisha> {
    final private int id;

    public FindByIdAfishaSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Afisha afisha) {
        return afisha.equalsWithId(this.id);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM afisha WHERE id=?;";
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
