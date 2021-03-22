public class Mailing {
    int id;
    String email;

    Mailing(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}