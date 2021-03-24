package news;

import java.util.List;

public class Category {
    final private int id;
    private String title;
    private List<New> news;

    public Category(int id) {
        super();
        this.id = id;
    }

    public Category(int id, String title, List<New> news) {
        this.id = id;
        this.title = title;
        this.news = news;
    }

    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getTitle() {
        return this.title;
    }
}
