package news.model;

import java.util.*;

public class Category {
    final private int id;
    private String title;
    final private Collection<Articles> articles = new ArrayList<>();

    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Category(int id, String title, Collection<Articles> articles) {
        this.id = id;
        this.title = title;
        this.articles.addAll(articles);
    }

    public void addNewArticle(Articles article) {
        this.articles.add(article);
    }

    public boolean containArticle(Articles article) {
        return this.articles.contains(article);
    }

    public void rename(String newTitle) {
        this.title = newTitle;
    }

    public String getTitle() {
        return this.title;
    }
}
