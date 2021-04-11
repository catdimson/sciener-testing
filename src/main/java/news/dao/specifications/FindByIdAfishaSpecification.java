package news.dao.specifications;

import news.model.Afisha;

public class FindByIdAfishaSpecification implements SqlSpecification<Afisha> {
    final private int id;

    public FindByIdAfishaSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Afisha afisha) {
        return (int) afisha.getObjects()[0] == this.id;
    }

    @Override
    public String toSqlClauses() {
        return String.format("SELECT * FROM afisha WHERE id='%d';", this.id);
    }
}
