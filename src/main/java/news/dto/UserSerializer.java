package news.dto;

import news.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс сериализации пользователя в JSON
 */
public class UserSerializer implements Serializer<User> {
    private String json;
    private User user;

    public UserSerializer(User user) {
        this.user = user;
    }

    public UserSerializer(String json) {
        this.json = json;
    }

    @Override
    public String toJSON() {
        String[] userFields = User.getFields();
        Object[] userInstance = user.getObjects();
        LocalDate lastLogin = (LocalDate) userInstance[6];
        LocalDate dateJoined = (LocalDate) userInstance[7];

        return "" +
            "{\n" +
            "\t" + "\"" + userFields[0] + "\"" + ":" + userInstance[0] + ",\n" +
            "\t" + "\"" + userFields[1] + "\"" + ":" + "\"" + userInstance[1] + "\"" + ",\n" +
            "\t" + "\"" + userFields[2] + "\"" + ":" + "\"" + userInstance[2] + "\"" + ",\n" +
            "\t" + "\"" + userFields[3] + "\"" + ":" + "\"" + userInstance[3] + "\"" + ",\n" +
            "\t" + "\"" + userFields[4] + "\"" + ":" + "\"" + userInstance[4] + "\"" + ",\n" +
            "\t" + "\"" + userFields[5] + "\"" + ":" + "\"" + userInstance[5] + "\"" + ",\n" +
            "\t" + "\"" + userFields[6] + "\"" + ":" + Timestamp.valueOf(lastLogin.atStartOfDay()).getTime() / 1000 + ",\n" +
            "\t" + "\"" + userFields[7] + "\"" + ":" + Timestamp.valueOf(dateJoined.atStartOfDay()).getTime() / 1000 + ",\n" +
            "\t" + "\"" + userFields[8] + "\"" + ":" + userInstance[8] + ",\n" +
            "\t" + "\"" + userFields[9] + "\"" + ":" + userInstance[9] + ",\n" +
            "\t" + "\"" + userFields[10] + "\"" + ":" + userInstance[10] + ",\n" +
            "\t" + "\"" + userFields[11] + "\"" + ":" + userInstance[11] + ",\n" +
            "}";
    }

    @Override
    public User toObject() {
        int id;
        String password;
        String username;
        String firstName;
        String lastName;
        String email;
        LocalDate lastLogin;
        LocalDate dateJoined;
        boolean isSuperuser = false;
        boolean isStaff = false;
        boolean isActive = false;
        int groupId;

        String[] lines = json.split("\n");
        /*for (int i = 0; i < lines.length; i++) {
            System.out.println(i + ":" + lines[i]);
        }*/

        // id
        Pattern p = Pattern.compile(":(\\d+),");
        Matcher m = p.matcher(lines[1]);
        m.find();
        id = Integer.parseInt(m.group(1));
        // password
        m = Pattern.compile(":\"(.+)\",").matcher(lines[2]);
        m.find();
        password = m.group(1);
        // username
        m = Pattern.compile(":\"(.+)\",").matcher(lines[3]);
        m.find();
        username = m.group(1);
        // firstName
        m = Pattern.compile(":\"(.+)\",").matcher(lines[4]);
        m.find();
        firstName = m.group(1);
        // lastName
        m = Pattern.compile(":\"(.+)\",").matcher(lines[5]);
        m.find();
        lastName = m.group(1);
        // email
        m = Pattern.compile(":\"(.+)\",").matcher(lines[6]);
        m.find();
        email = m.group(1);
        // lastLogin
        m = Pattern.compile(":(\\d+),").matcher(lines[7]);
        m.find();
        int timestampLastLogin = Integer.parseInt(m.group(1));
        lastLogin = Timestamp.from(Instant.ofEpochSecond(timestampLastLogin)).toLocalDateTime().toLocalDate();
        // dateJoined
        m = Pattern.compile(":(\\d+),").matcher(lines[8]);
        m.find();
        int timestampDateJoined= Integer.parseInt(m.group(1));
        dateJoined = Timestamp.from(Instant.ofEpochSecond(timestampDateJoined)).toLocalDateTime().toLocalDate();
        // isSuperuser
        m = Pattern.compile(":(\\w{4,5}),").matcher(lines[9]);
        if (m.find()) {
            if (m.group(1).equals("true")) {
                isSuperuser = true;
            }
            if (m.group(1).equals("false")) {
                isSuperuser = false;
            }
        }
        // isStaff
        m = Pattern.compile(":(\\w{4,5}),").matcher(lines[10]);
        if (m.find()) {
            if (m.group(1).equals("true")) {
                isStaff = true;
            }
            if (m.group(1).equals("false")) {
                isStaff = false;
            }
        }
        // isActive
        m = Pattern.compile(":(\\w{4,5}),").matcher(lines[11]);
        if (m.find()) {
            if (m.group(1).equals("true")) {
                isActive = true;
            }
            if (m.group(1).equals("false")) {
                isActive = false;
            }
        }
        // groupId
        m = Pattern.compile(":(\\d+),").matcher(lines[12]);
        m.find();
        groupId = Integer.parseInt(m.group(1));

        // создаем по распарсеным данным объект пользователя
        User user = new User(id, password, username, firstName, lastName, email, lastLogin, dateJoined, isSuperuser,
                isStaff, isActive, groupId);

        return user;
    }
}
