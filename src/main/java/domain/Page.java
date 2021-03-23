public class Page {
    int id;
    String title;
    String metaCharset;
    String metaDescription;
    String metaKeywords;
    String titleMenu;
    String faviconPath;
    boolean isPublished;
    String url;
    Content content;

    Page(int id, String title, String metaCharset, String metaDescription, String metaKeywords, String titleMenu,
         String faviconPath, boolean isPublished, String url, Content content) {
        this.id = id;
        this.title = title;
        this.metaCharset = metaCharset;
        this.metaDescription = metaDescription;
        this.metaKeywords = metaKeywords;
        this.titleMenu = titleMenu;
        this.faviconPath = faviconPath;
        this.isPublished = isPublished;
        this.url = url;
        this.content = content;
    }

    public void edit(String title, String metaCharset, String metaDescription, String metaKeywords, String titleMenu,
                     String faviconPath, boolean isPublished, String url, Content content) {
        this.title = title;
        this.metaCharset = metaCharset;
        this.metaDescription = metaDescription;
        this.metaKeywords = metaKeywords;
        this.titleMenu = titleMenu;
        this.faviconPath = faviconPath;
        this.isPublished = isPublished;
        this.url = url;
        this.content = content;
    }
}
