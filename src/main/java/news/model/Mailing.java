package news.model;

/**
 * Рассылка
 * */
public class Mailing {
    private int id;
    private String email;

    public Mailing(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public Mailing(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean sendMessage(User user) throws IllegalAccessException {
        if (user.isPermissionOfSuperuser()) {
            /* Код отправки письма */
            return true;
        } else {
            throw new IllegalAccessException("Недостаточно прав");
        }
    }
}