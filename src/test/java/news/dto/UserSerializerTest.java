package news.dto;

import news.model.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserSerializerTest {
    private static LocalDate lastLogin;
    private static LocalDate dateJoined;

    @BeforeAll
    static void beforeAll() {
        dateJoined = LocalDate.of(2019, 4, 25);
        lastLogin = LocalDate.of(2019, 6, 25);
    }

    @Test
    void toJSON() {
        User user = new User(1,"qwerty123", "alex1993", "Александр", "Колесников",
                "alex1993@mail.ru", lastLogin, dateJoined, true, true, true, 1);
        final String expectedJSON =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"password\":\"qwerty123\",\n" +
                "\t\"username\":\"alex1993\",\n" +
                "\t\"firstName\":\"Александр\",\n" +
                "\t\"lastName\":\"Колесников\",\n" +
                "\t\"email\":\"alex1993@mail.ru\",\n" +
                "\t\"lastLogin\":" + Timestamp.valueOf(lastLogin.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"dateJoined\":" + Timestamp.valueOf(dateJoined.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"isSuperuser\":true,\n" +
                "\t\"isStaff\":true,\n" +
                "\t\"isActive\":true,\n" +
                "\t\"groupId\":1\n" +
                "}";

        UserSerializer userSerializer = new UserSerializer(user);
        String result = userSerializer.toJSON();

        assertThat(result).isEqualTo(expectedJSON);
    }

    @Test
    void toObject() {
        SoftAssertions soft = new SoftAssertions();
        final String json =
                "{\n" +
                "\t\"id\":1,\n" +
                "\t\"password\":\"qwerty123\",\n" +
                "\t\"username\":\"alex1993\",\n" +
                "\t\"firstName\":\"Александр\",\n" +
                "\t\"lastName\":\"Колесников\",\n" +
                "\t\"email\":\"alex1993@mail.ru\",\n" +
                "\t\"lastLogin\":" + Timestamp.valueOf(lastLogin.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"dateJoined\":" + Timestamp.valueOf(dateJoined.atStartOfDay()).getTime() / 1000 + ",\n" +
                "\t\"isSuperuser\":true,\n" +
                "\t\"isStaff\":true,\n" +
                "\t\"isActive\":true,\n" +
                "\t\"groupId\":1\n" +
                "}";

        UserSerializer userSerializer = new UserSerializer(json);
        User user = userSerializer.toObject();

        // сверяем данные
        soft.assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("password", "qwerty123")
                .hasFieldOrPropertyWithValue("username", "alex1993")
                .hasFieldOrPropertyWithValue("firstName", "Александр")
                .hasFieldOrPropertyWithValue("lastName", "Колесников")
                .hasFieldOrPropertyWithValue("email", "alex1993@mail.ru")
                .hasFieldOrPropertyWithValue("lastLogin", lastLogin)
                .hasFieldOrPropertyWithValue("dateJoined", dateJoined)
                .hasFieldOrPropertyWithValue("isSuperuser", true)
                .hasFieldOrPropertyWithValue("isStaff", true)
                .hasFieldOrPropertyWithValue("isActive", true)
                .hasFieldOrPropertyWithValue("groupId", 1);
        soft.assertAll();
    }
}