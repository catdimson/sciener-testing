package news.dao.specifications;

import news.model.Comment;

public class FindByUserIdCommentSpecification implements SqlSpecification<Comment> {
    final private int userId;

    public FindByUserIdCommentSpecification(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean isSpecified(Comment comment) {
        return (int) comment.getObjects()[1] == this.userId;
    }

    @Override
    public String toSqlClauses() {

        return String.format(
                "SELECT * FROM comment LEFT JOIN attachment " +
                "ON comment.id = attachment.comment_id " +
                "WHERE comment.user_id=%d", this.userId);
    }

    public boolean isById() {
        return false;
    }
}
