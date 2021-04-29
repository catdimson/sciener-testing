package news.dao.repositories;

import news.dao.connection.DBPool;
import news.dao.specifications.FindAllMailingSpecification;
import news.dao.specifications.FindByEmailMailingSpecification;
import news.dao.specifications.FindByIdMailingSpecification;
import news.model.Mailing;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MailingRepositoryTest {
    private PostgreSQLContainer container;
    private DBPool poolConnection;

    @BeforeEach
    void setUp() throws SQLException {
        this.container = new PostgreSQLContainer("postgres")
                .withUsername("admin")
                .withPassword("qwerty")
                .withDatabaseName("news");
        this.container.start();

        String sqlCreateTableMailing = "CREATE TABLE IF NOT EXISTS mailing (" +
                "id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 )," +
                "email character varying(80) NOT NULL," +
                "CONSTRAINT mailing_pk PRIMARY KEY (id)," +
                "CONSTRAINT email_unique UNIQUE (email)" +
                ");";
        this.poolConnection = new DBPool(this.container.getJdbcUrl(), this.container.getUsername(), this.container.getPassword());

        Statement statement = this.poolConnection.getConnection().createStatement();
        statement.executeUpdate(sqlCreateTableMailing);
    }

    @Test
    void findById() {
        try {
            SoftAssertions soft = new SoftAssertions();
            MailingRepository mailingRepository = new MailingRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO mailing (email) VALUES('test@mail.ru');";
            statement.executeUpdate(sqlInsertInstance);
            Mailing mailing = new Mailing(1,"test@mail.ru");

            FindByIdMailingSpecification findById = new FindByIdMailingSpecification(1);
            List<Mailing> resultFindByIdMailing = mailingRepository.query(findById);

            soft.assertThat(mailing)
                    .hasFieldOrPropertyWithValue("id", resultFindByIdMailing.get(0).getObjects()[0])
                    .hasFieldOrPropertyWithValue("email", resultFindByIdMailing.get(0).getObjects()[1]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void findByEmail() {
        try {
            SoftAssertions soft = new SoftAssertions();
            MailingRepository mailingRepository = new MailingRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO mailing (email) VALUES('test@mail.ru');";
            statement.executeUpdate(sqlInsertInstance);
            Mailing mailing = new Mailing(1,"test@mail.ru");

            FindByEmailMailingSpecification findByEmail = new FindByEmailMailingSpecification("test@mail.ru");
            List<Mailing> resultFindByIdMailing = mailingRepository.query(findByEmail);

            soft.assertThat(mailing)
                    .hasFieldOrPropertyWithValue("id", resultFindByIdMailing.get(0).getObjects()[0])
                    .hasFieldOrPropertyWithValue("email", resultFindByIdMailing.get(0).getObjects()[1]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void findAll() {
        try {
            SoftAssertions soft = new SoftAssertions();
            MailingRepository mailingRepository = new MailingRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO mailing (email) VALUES ('test@mail.ru'), ('test2@mail.ru');";
            statement.executeUpdate(sqlInsertInstance);
            Mailing mailing1 = new Mailing(1,"test@mail.ru");
            Mailing mailing2 = new Mailing(2,"test2@mail.ru");

            FindAllMailingSpecification findAll = new FindAllMailingSpecification();
            List<Mailing> resultFindAllMailing = mailingRepository.query(findAll);

            soft.assertThat(mailing1)
                    .hasFieldOrPropertyWithValue("id", resultFindAllMailing.get(0).getObjects()[0])
                    .hasFieldOrPropertyWithValue("email", resultFindAllMailing.get(0).getObjects()[1]);
            soft.assertAll();
            soft.assertThat(mailing2)
                    .hasFieldOrPropertyWithValue("id", resultFindAllMailing.get(1).getObjects()[0])
                    .hasFieldOrPropertyWithValue("email", resultFindAllMailing.get(1).getObjects()[1]);
            soft.assertAll();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void createMailing() {
        try {
            MailingRepository mailingRepository = new MailingRepository(this.poolConnection);
            Mailing mailing = new Mailing("test@mail.ru");

            mailingRepository.create(mailing);

            Connection connection = this.poolConnection.getConnection();
            String sqlQueryInstanceFromTableMailing = "SELECT id, email FROM mailing WHERE email='test@mail.ru'";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sqlQueryInstanceFromTableMailing);
            result.next();
            assertThat(mailing).hasFieldOrPropertyWithValue("email", result.getString(2));
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void deleteMailing() {
        try {
            MailingRepository mailingRepository = new MailingRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO mailing (email) VALUES('test@mail.ru');";
            statement.executeUpdate(sqlInsertInstance, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            mailingRepository.delete(generatedKeys.getInt(1));

            String sqlQueryInstance = String.format("SELECT id, email FROM mailing WHERE id=%d;", generatedKeys.getInt(1));
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            assertThat(result.next()).as("Запись класса Mailing не была удалена").isFalse();
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void updateMailing() {
        try {
            MailingRepository mailingRepository = new MailingRepository(this.poolConnection);
            Connection connection = this.poolConnection.getConnection();
            Statement statement = connection.createStatement();
            String sqlInsertInstance = "INSERT INTO mailing (email) VALUES('test@mail.ru');";
            statement.executeUpdate(sqlInsertInstance);
            Mailing mailing = new Mailing(1, "test2@yandex.ru");
            Object[] instance = mailing.getObjects();

            mailingRepository.update(mailing);

            String sqlQueryInstance = String.format("SELECT id, email FROM mailing WHERE id=%s;", instance[0]);
            ResultSet result = statement.executeQuery(sqlQueryInstance);
            result.next();
            assertThat(mailing).hasFieldOrPropertyWithValue("email", result.getString(2));
            this.poolConnection.pullConnection(connection);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}