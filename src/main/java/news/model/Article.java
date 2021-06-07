package news.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Новость
 */
@Entity
@Table(name = "article", schema = "public", catalog = "news_db")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "create_date")
    private Timestamp createDate;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "lead")
    private String lead;

    @Basic
    @Column(name = "edit_date")
    private Timestamp editDate;

    @Basic
    @Column(name = "text")
    private String text;

    @Basic
    @Column(name = "is_published")
    private boolean isPublished;

    @Basic
    @Column(name = "category_id")
    private int categoryId;

    @Basic
    @Column(name = "user_id")
    private int userId;

    @Basic
    @Column(name = "source_id")
    private int sourceId;

    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<ArticleImage> images = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "article_tag",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Collection<Tag> tags = new ArrayList<>();

    public Article() {}

    public Article(int id, String title, String lead, Timestamp createDate, Timestamp editDate, String text,
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

    public Article(String title, String lead, Timestamp createDate, Timestamp editDate, String text,
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

    public int getArticleId() {
        return this.id;
    }

    public void setArticleId(int id) {
        this.id = id;
    }

    /**
     * Редактирование новости
     */
    public void edit(String title, String lead, Timestamp editDate, String text, boolean isPublished, int sourceId) {
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

    public Collection<ArticleImage> getImages() {
        return this.images;
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
    public void addNewTag(Tag tag) {
        this.tags.add(tag);
    }
    /**
     * Замена новым списком id тегов
     */
    public void setAllTags(Collection<Tag> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    public Collection<Tag> getTags() {
        return this.tags;
    }
    /**
     * Содержится ли данный id тега в списке
     */
    public boolean containTag(Tag tag) {
        return this.tags.contains(tag);
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
                tags
        };
    }

    public static String[] getFields() {
        return new String[] {
            "id", "title", "lead", "createDate", "editDate", "text", "isPublished", "categoryId",
                "userId", "sourceId", "images", "tagsId"
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article that = (Article) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(lead, that.lead) && Objects.equals(createDate, that.createDate) && Objects.equals(editDate, that.editDate) && Objects.equals(text, that.text) && Objects.equals(isPublished, that.isPublished);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, lead, createDate, editDate, text, isPublished);
    }
}
