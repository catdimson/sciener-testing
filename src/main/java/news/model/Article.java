package news.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Новость
 */
public class Article {
    private int id;
    final private LocalDate createDate;
    private String title;
    private String lead;
    private LocalDate editDate;
    private String text;
    private boolean isPublished;
    private int categoryId;
    private int userId;
    private int sourceId;
    private Collection<ArticleImage> images = new ArrayList<>();

    public Article(int id, String title, String lead, LocalDate createDate, LocalDate editDate, String text,
                   boolean isPublished, int categoryId, int userId, int sourceId) {
        this.id = id;
        this.title = title;
        this.lead = lead;
        this.createDate = createDate;
        this.editDate = editDate;
        this.text = text;
        this.isPublished = isPublished;
        this.categoryId = categoryId;
        this.userId = userId;
        this.sourceId = sourceId;
    }

    public Article(String title, String lead, LocalDate createDate, LocalDate editDate, String text,
                   boolean isPublished, int categoryId, int userId, int sourceId) {
        this.title = title;
        this.lead = lead;
        this.createDate = createDate;
        this.editDate = editDate;
        this.text = text;
        this.isPublished = isPublished;
        this.categoryId = categoryId;
        this.userId = userId;
        this.sourceId = sourceId;
    }

    static class ArticleImage {
        private int id;
        private String title;
        private String path;

        public ArticleImage(int id, String title, String path) {
            this.id = id;
            this.title = title;
            this.path = path;
        }

        public ArticleImage(String title, String path) {
            this.title = title;
            this.path = path;
        }
    }

    /**
     * Редактирование новости
     */
    public void edit(String title, String lead, LocalDate editDate, String text, boolean isPublished,
                     int categoryId, int userId, int sourceId) {
        this.title = title;
        this.lead = lead;
        this.editDate = editDate;
        this.text = text;
        this.isPublished = isPublished;
        this.categoryId = categoryId;
        this.userId = userId;
        this.sourceId = sourceId;
    }

    public void addNewImage(ArticleImage articleImage) {
        this.images.add(articleImage);
    }

    public boolean containImage(ArticleImage articleImage) {
        return this.images.contains(articleImage);
    }

    public void published() {
        this.isPublished = true;
    }

    public void unpublished() {
        this.isPublished = false;
    }

    public boolean getStatusPublished() {
        return this.isPublished;
    }
}
