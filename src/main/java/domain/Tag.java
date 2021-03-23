import java.util.List;

public class Tag {
    int id;
    String title;
    List<New> news;

    Tag(int id, String title) {
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
