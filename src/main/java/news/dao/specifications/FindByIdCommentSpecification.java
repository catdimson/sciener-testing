package news.dao.specifications;

import news.model.Comment;

public class FindByIdCommentSpecification implements ExtendSqlSpecification<Comment> {
    final private int id;

    public FindByIdCommentSpecification(int id) {
        this.id = id;
    }

    @Override
    public boolean isSpecified(Comment comment) {
        return (int) comment.getObjects()[0] == this.id;
    }

    @Override
    public String toSqlClauses() {
        return String.format("" +
                "SELECT * FROM comment JOIN attachment " +
                "ON comment.id = attachment.comment_id " +
                "WHERE comment.id=%d;", this.id);
    }

    public boolean isById() {
        return true;
    }
}
