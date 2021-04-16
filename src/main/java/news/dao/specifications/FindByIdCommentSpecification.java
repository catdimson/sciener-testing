package news.dao.specifications;

import news.model.Comment;

public class FindByIdCommentSpecification implements ExtendSqlSpecification<Comment> {
    final private int id;

    public FindByIdCommentSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Comment comment) {
        return comment.equalsWithId(this.id);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM comment " +
                "JOIN attachment " +
                "ON comment.id = attachment.comment_id " +
                "WHERE comment.id=?;";
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
