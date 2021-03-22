import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

class UserTest {

    @Test
    void User() {
        Date lastLogin = new Date();
        Date dateJoined = new Date();
        User user = new User("qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", 1, lastLogin, dateJoined, true, true, true);
        var soft = new SoftAssertions();

        soft.assertThat(user)
            .hasFieldOrPropertyWithValue("password", "qwerty12")
            .hasFieldOrPropertyWithValue("username", "admin")
            .hasFieldOrPropertyWithValue("firstName", "alexandr")
            .hasFieldOrPropertyWithValue("lastName", "kanonenko")
            .hasFieldOrPropertyWithValue("email", "admin@gmail.com")
            .hasFieldOrPropertyWithValue("group", 1)
            .hasFieldOrPropertyWithValue("lastLogin", lastLogin)
            .hasFieldOrPropertyWithValue("dateJoined", dateJoined)
            .hasFieldOrPropertyWithValue("isSuperuser", true)
            .hasFieldOrPropertyWithValue("isStaff", true)
            .hasFieldOrPropertyWithValue("isActive", true);

        soft.assertAll();
    }

    @Test
    void getFullName() {
        Date lastLogin = new Date();
        Date dateJoined = new Date();
        User user = new User("qwerty12", "admin", "alexandr", "kanonenko",
                "admin@gmail.com", 1, lastLogin, dateJoined, true, true, true);
        user.changeFirstName("Олег");
        user.changeLastName("Бочаров");

        String expected = "Олег Бочаров";

        Assertions.assertEquals(user.getFullName(), expected);
    }
}



