package news.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Integer> tagsId = new HashSet<>();

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

    /**
     * Изображение новости
     */
    public static class ArticleImage {
        private int id;
        private String title;
        private String path;
        private int articleId;

        public ArticleImage(int id, String title, String path, int articleId) {
            this.id = id;
            this.title = title;
            this.path = path;
            this.articleId = articleId;
        }

        public ArticleImage(String title, String path, int articleId) {
            this.title = title;
            this.path = path;
            this.articleId = articleId;
        }

        public Object[] getObjects() {
            return new Object[] {
                    id,
                    title,
                    path,
                    articleId
            };
        }

        public static String[] getFields() {
            return new String[] {
                    "id", "title", "path", "articleId"
            };
        }
    }

    /**
     * Редактирование новости
     */
    public void edit(String title, String lead, LocalDate editDate, String text, boolean isPublished, int sourceId) {
        this.title = title;
        this.lead = lead;
        this.editDate = editDate;
        this.text = text;
        this.isPublished = isPublished;
        this.sourceId = sourceId;
    }

    /**
     * Добавить изображение к новости
     */
    public void addNewImage(ArticleImage articleImage) {
        this.images.add(articleImage);
    }
    /**
     * Замена новым списком изображений
     */
    public void setAllImages(ArrayList<ArticleImage> images) {
        this.images.clear();
        this.images.addAll(images);
    }
    /**
     * Содержится ли данное изображение в списке
     */
    public boolean containImage(ArticleImage articleImage) {
        return this.images.contains(articleImage);
    }

    /**
     * Добавить id тега
     */
    public void addNewTagId(Integer tagId) {
        this.tagsId.add(tagId);
    }
    /**
     * Замена новым списком id тегов
     */
    public void setAllTagsId(ArrayList<Integer> tagsId) {
        this.tagsId.clear();
        this.tagsId.addAll(tagsId);
    }
    /**
     * Содержится ли данный id тега в списке
     */
    public boolean containTag(Integer tagId) {
        return this.tagsId.contains(tagId);
    }

    /**
     * Опубликовать новость
     */
    public void published() {
        this.isPublished = true;
    }

    /**
     * Снять с публикации
     */
    public void unpublished() {
        this.isPublished = false;
    }

    public boolean getStatusPublished() {
        return this.isPublished;
    }

    public boolean equalsWithId(int id) {
        return this.id == id;
    }

    public boolean equalsWithTitle(String title) {
        return this.title.equals(title);
    }

    public Object[] getObjects() {
        return new Object[] {
                id,
                title,
                lead,
                createDate,
                editDate,
                text,
                isPublished,
                categoryId,
                userId,
                sourceId,
                images,
                tagsId
        };
    }

    public static String[] getFields() {
        return new String[] {
            "id", "title", "lead", "createDate", "editDate", "text", "isPublished", "categoryId",
                "userId", "sourceId", "images", "tagsId"
        };
    }
}
