package domain;

public class Mailing {
    final private int id;
    final private String email;

    public Mailing(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}