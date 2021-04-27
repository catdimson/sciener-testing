package news.model;

/**
 * Рассылка
 */
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

    /**
     * Отправка письма
     */
    public boolean sendMessage(User user) throws IllegalAccessException {
        if (user.isPermissionOfSuperuser()) {
            /* Код отправки письма */
            return true;
        } else {
            throw new IllegalAccessException("Недостаточно прав");
        }
    }

    public boolean equalsWithId(int id) {
        return this.id == id;
    }

    public boolean equalsWithEmail(String email) {
        return this.email.equals(email);
    }

    public Object[] getObjects() {
        return new Object[] {
                id,
                email
        };
    }

    public static String[] getFields() {
        return new String[] {
                "id", "email"
        };
    }
}