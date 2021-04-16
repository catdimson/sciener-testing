package news.dao.specifications;

import news.model.Comment;

public class FindByUserIdCommentSpecification implements ExtendSqlSpecification<Comment> {
    final private int userId;

    public FindByUserIdCommentSpecification(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean isSpecified(Comment comment) {
        return comment.equalsWithUserId(this.userId);
    }

    @Override
    public String toSqlClauses() {
        return "SELECT * FROM comment LEFT JOIN attachment " +
                "ON comment.id = attachment.comment_id " +
                "WHERE comment.user_id=? ORDER BY attachment.comment_id DESC";
    }

    @Override
    public Object getCriterial() {
        return this.userId;
    }

    @Override
    public boolean isById() {
        return false;
    }
}
