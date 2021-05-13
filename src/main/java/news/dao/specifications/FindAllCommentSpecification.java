package news.dao.specifications;

import news.model.Comment;

public class FindAllCommentSpecification implements ExtendSqlSpecification<Comment> {

    @Override
    public boolean isSpecified(Comment comment) {
        return true;
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM comment LEFT JOIN attachment " +
                "ON comment.id = attachment.comment_id " +
                "ORDER BY 1";
    }

    @Override
    public Object getCriterial() {
        return null;
    }

    @Override
    public boolean isById() {
        return false;
    }
}
