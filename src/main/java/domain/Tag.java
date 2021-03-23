package domain;

import java.util.List;

public class Tag {
    final private int id;
    private String title;
    private List<New> news;

    public Tag(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getTitle() {
        return this.title;
    }
}
