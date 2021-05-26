package news.service.bank;

import news.model.bank.CourseValute;
import news.model.bank.Valute;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("Тестирование BankService")
@RestClientTest(BankService.class)
class BankServiceTest {

    @Autowired
    private BankService bankService;

    @Autowired
    private MockRestServiceServer server;

    @DisplayName("Успешный ответ от BankService")
    @Test
    void successGetCourseValute() throws IOException {
        String body = new String(Files.readAllBytes(Paths.get("src/test/resources/valutes.json")));
        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo("https://www.cbr-xml-daily.ru/daily_json.js")
                ).andRespond(
                        withSuccess(body, MediaType.APPLICATION_JSON)
        );
        Valute usd = bankService.getCourseValute().getValutes().getUSD();
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(usd)
                .hasFieldOrPropertyWithValue("id", "R01235")
                .hasFieldOrPropertyWithValue("charCode", "USD")
                .hasFieldOrPropertyWithValue("value", new BigDecimal("73.3963"));
        soft.assertAll();
    }

    @DisplayName("Провальный ответ от BankService")
    @Test
    void invalidGetCourseValute() {
        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo("https://www.cbr-xml-daily.ru/daily_json.js")
                ).andRespond(
                withBadRequest()
        );
        CourseValute courseValute = bankService.getCourseValute();
        assertThat(courseValute).as("CourseValute, получаемый от BankService должен быть null").isNull();
    }
}