package news.dto;

import news.model.User;

import java.sql.Timestamp;
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
        Timestamp lastLogin = (Timestamp) userInstance[6];
        Timestamp dateJoined = (Timestamp) userInstance[7];

        return "" +
            "{\n" +
            "\t" + "\"" + userFields[0] + "\"" + ":" + userInstance[0] + ",\n" +
            "\t" + "\"" + userFields[1] + "\"" + ":" + "\"" + userInstance[1] + "\"" + ",\n" +
            "\t" + "\"" + userFields[2] + "\"" + ":" + "\"" + userInstance[2] + "\"" + ",\n" +
            "\t" + "\"" + userFields[3] + "\"" + ":" + "\"" + userInstance[3] + "\"" + ",\n" +
            "\t" + "\"" + userFields[4] + "\"" + ":" + "\"" + userInstance[4] + "\"" + ",\n" +
            "\t" + "\"" + userFields[5] + "\"" + ":" + "\"" + userInstance[5] + "\"" + ",\n" +
            "\t" + "\"" + userFields[6] + "\"" + ":" + lastLogin.getTime() / 1000 + ",\n" +
            "\t" + "\"" + userFields[7] + "\"" + ":" + dateJoined.getTime() / 1000 + ",\n" +
            "\t" + "\"" + userFields[8] + "\"" + ":" + userInstance[8] + ",\n" +
            "\t" + "\"" + userFields[9] + "\"" + ":" + userInstance[9] + ",\n" +
            "\t" + "\"" + userFields[10] + "\"" + ":" + userInstance[10] + ",\n" +
            "\t" + "\"" + userFields[11] + "\"" + ":" + userInstance[11] + "\n" +
            "}";
    }

    @Override
    public User toObject() {
        int id = 0;
        String password;
        String username;
        String firstName;
        String lastName;
        String email;
        Timestamp lastLogin;
        Timestamp dateJoined;
        boolean isSuperuser = false;
        boolean isStaff = false;
        boolean isActive = false;
        int groupId;
        int indexLine = 1;
        boolean withId;

        String[] lines = json.split("\n");
        /*for (int i = 0; i < lines.length; i++) {
            System.out.println(i + ":" + lines[i]);
        }*/
        Pattern p = Pattern.compile("\"id\":.+");
        Matcher m = p.matcher(lines[indexLine]);
        withId = m.find();
        if (withId) {
            p = Pattern.compile(":(\\d+),");
            m = p.matcher(lines[indexLine]);
            m.find();
            id = Integer.parseInt(m.group(1));
            indexLine++;
        }
        // password
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        password = m.group(1);
        indexLine++;
        // username
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        username = m.group(1);
        indexLine++;
        // firstName
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        firstName = m.group(1);
        indexLine++;
        // lastName
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        lastName = m.group(1);
        indexLine++;
        // email
        m = Pattern.compile(":\"(.+)\",").matcher(lines[indexLine]);
        m.find();
        email = m.group(1);
        indexLine++;
        // lastLogin
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]);
        m.find();
        lastLogin = new Timestamp(Long.parseLong(m.group(1)));
        indexLine++;
        // dateJoined
        m = Pattern.compile(":(\\d+),").matcher(lines[indexLine]);
        m.find();
        dateJoined = new Timestamp(Long.parseLong(m.group(1)));
        indexLine++;
        // isSuperuser
        m = Pattern.compile(":(\\w{4,5}),").matcher(lines[indexLine]);
        if (m.find()) {
            if (m.group(1).equals("true")) {
                isSuperuser = true;
            }
            if (m.group(1).equals("false")) {
                isSuperuser = false;
            }
        }
        indexLine++;
        // isStaff
        m = Pattern.compile(":(\\w{4,5}),").matcher(lines[indexLine]);
        if (m.find()) {
            if (m.group(1).equals("true")) {
                isStaff = true;
            }
            if (m.group(1).equals("false")) {
                isStaff = false;
            }
        }
        indexLine++;
        // isActive
        m = Pattern.compile(":(\\w{4,5}),").matcher(lines[indexLine]);
        if (m.find()) {
            if (m.group(1).equals("true")) {
                isActive = true;
            }
            if (m.group(1).equals("false")) {
                isActive = false;
            }
        }
        indexLine++;
        // groupId
        m = Pattern.compile(":(\\d+)").matcher(lines[indexLine]);
        m.find();
        groupId = Integer.parseInt(m.group(1));

        // создаем по распарсеным данным объект пользователя
        User user;
        if (withId) {
            user = new User(id, password, username, firstName, lastName, email, lastLogin, dateJoined, isSuperuser,
                    isStaff, isActive, groupId);
        } else {
            user = new User(password, username, firstName, lastName, email, lastLogin, dateJoined, isSuperuser,
                    isStaff, isActive, groupId);
        }

        return user;
    }
}
